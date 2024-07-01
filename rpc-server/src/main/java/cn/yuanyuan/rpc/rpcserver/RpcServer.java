package cn.yuanyuan.rpc.rpcserver;

import cn.yuanyuan.rpc.rpccommon.bean.RpcRequest;
import cn.yuanyuan.rpc.rpccommon.bean.RpcResponse;
import cn.yuanyuan.rpc.rpccommon.codec.RpcDecoder;
import cn.yuanyuan.rpc.rpccommon.codec.RpcEncoder;
import cn.yuanyuan.rpc.rpccommon.util.CollectionUtil;
import cn.yuanyuan.rpc.rpccommon.util.StringUtil;
import cn.yuanyuan.rpc.rpcregistry.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * rpc 服务 用于发布rpc服务
 *
 * @author wuyitao
 */
public class RpcServer implements ApplicationContextAware, InitializingBean, DisposableBean {

    public static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

    private String serviceAddress;

    private ServiceRegistry serviceRegistry;

    private Map<String, Object> handlerMap = new HashMap<>();

    private ChannelFuture future;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;


    public RpcServer(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public RpcServer(String serviceAddress, ServiceRegistry serviceRegistry) {
        this.serviceAddress = serviceAddress;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        // 扫描带有 RpcService 注解的类并初始化 handlerMap 对象
        Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class);
        if (CollectionUtil.isNotEmpty(serviceBeanMap)) {
            for (Object serviceBean : serviceBeanMap.values()) {
                RpcService rpcService = serviceBean.getClass().getAnnotation(RpcService.class);
                String serviceName = rpcService.value().getName();
                String serviceVersion = rpcService.version();
                if (StringUtil.isNotBlank(serviceVersion)) {
                    serviceName += "-" + serviceVersion;
                }
                handlerMap.put(serviceName, serviceBean);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        registerService();
        openRpcService();
    }

    private void openRpcService() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        try {
            // 创建并初始化 Netty 服务端 Bootstrap 对象
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel channel) throws Exception {
                    ChannelPipeline pipeline = channel.pipeline();
                    pipeline.addLast(new RpcDecoder(RpcRequest.class)); // 解码 RPC 请求
                    pipeline.addLast(new RpcEncoder(RpcResponse.class)); // 编码 RPC 响应
                    pipeline.addLast(new RpcServerHandler(handlerMap)); // 处理 RPC 请求
                }
            });
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            // 获取 RPC 服务器的 IP 地址与端口号
            String[] addressArray = StringUtil.split(serviceAddress, ":");
            String ip = addressArray[0];
            int port = Integer.parseInt(addressArray[1]);
            LOGGER.info("rpc服务netty绑定地址：{}；端口：{}", ip, port);
            // 启动 RPC 服务器
            future = bootstrap.bind(ip, port).sync();
        } catch (Exception e) {
            LOGGER.error("rpc服务中断", e);
        }
    }

    private void registerService() {
        // 注册 RPC 服务地址
        if (serviceRegistry != null) {
            for (String interfaceName : handlerMap.keySet()) {
                serviceRegistry.register(interfaceName, serviceAddress);
                LOGGER.debug("register service: {} => {}", interfaceName, serviceAddress);
            }
        }
    }

    @Override
    public void destroy() throws Exception {
        // 关闭 RPC 服务器
        LOGGER.error("netty服务停止");
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        future.channel().closeFuture().sync();
        LOGGER.info("关闭netty监听");
    }
}
