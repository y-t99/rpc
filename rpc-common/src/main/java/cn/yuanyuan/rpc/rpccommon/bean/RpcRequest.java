package cn.yuanyuan.rpc.rpccommon.bean;

import lombok.Data;

/**
 * 封装 RPC 请求
 *
 * @author wuyitao
 */
@Data
public class RpcRequest {
    private String requestId;
    private String interfaceName;
    private String serviceVersion;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;
}
