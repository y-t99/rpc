package com.example.demo;


import cn.yuanyuan.rpc.rpcserver.RpcService;
import com.example.sampleapidemo.HelloService;
import com.example.sampleapidemo.Person;

@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(String name) {
        return "Hello! " + name;
    }

    @Override
    public String hello(Person person) {
        return "Hello! " + person.getFirstName() + " " + person.getLastName();
    }
}
