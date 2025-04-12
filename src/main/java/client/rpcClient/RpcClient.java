package client.rpcClient;

import common.Message.RpcRequest;
import common.Message.RpcResponse;

public interface RpcClient {
    //定义底层通信的方法
    RpcResponse sendRequest(RpcRequest request);
}
