package cn.yuanyuan.rpc.rpcregistry;

/**
 * 服务注册接口
 *
 * @author wuyitao
 */
public interface ServiceRegistry {

    /**
     * 注册服务名称与服务地址
     *
     * @param serviceName    服务名称
     * @param serviceAddress 服务地址
     */
    void register(String serviceName, String serviceAddress);
}