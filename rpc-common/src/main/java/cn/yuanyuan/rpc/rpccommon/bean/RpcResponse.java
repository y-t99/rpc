package cn.yuanyuan.rpc.rpccommon.bean;

import lombok.Data;

/**
 * 封装 RPC 响应
 *
 * @author wuyitao
 */
@Data
public class RpcResponse {

    private String requestId;
    private Exception exception;
    private Object result;
    public boolean hasException() {
        return exception != null;
    }
}
