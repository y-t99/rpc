package cn.yuanyuan.rpc.rpcregistryzookper;

import cn.yuanyuan.rpc.rpcregistry.ServiceRegistry;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;


/**
 * 基于 ZooKeeper 的服务注册接口实现
 *
 * @author wuyitao
 */
public class ZooKeeperServiceRegistry implements ServiceRegistry, DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperServiceRegistry.class);

    private ZkClient zkClient;

    public ZooKeeperServiceRegistry(String zkAddress) {
        try {
            // 创建 ZooKeeper 客户端
            zkClient = new ZkClient(zkAddress, Constant.ZK_SESSION_TIMEOUT, Constant.ZK_CONNECTION_TIMEOUT);
            LOGGER.info("连接 zookeeper, 地址: {}", zkAddress);
        } catch (Exception e) {
            LOGGER.error("zkClient创建失败", e);
        }
    }

    @Override
    public void register(String serviceName, String serviceAddress) {
        // 创建 registry 节点（持久）
        String registryPath = Constant.ZK_REGISTRY_PATH;
        if (!zkClient.exists(registryPath)) {
            zkClient.createPersistent(registryPath);
            LOGGER.debug("create registry node: {}", registryPath);
        }
        // 创建 service 节点（持久）
        String servicePath = registryPath + "/" + serviceName;
        if (!zkClient.exists(servicePath)) {
            zkClient.createPersistent(servicePath);
            LOGGER.debug("create service node: {}", servicePath);
        }
        // 创建 address 节点（临时）
        String addressPath = servicePath + "/address-";
        String addressNode = zkClient.createEphemeralSequential(addressPath, serviceAddress);
        LOGGER.debug("create address node: {}", addressNode);
    }

    @Override
    public void destroy() throws Exception {
        if (zkClient !=null ) {
            zkClient.close();
            LOGGER.info("关闭与zookeeper的连接");
        }
    }
}