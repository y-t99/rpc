package com.example.sampleclientdemo;

import cn.yuanyuan.rpc.rpcclient.RpcProxy;
import cn.yuanyuan.rpc.rpcregistry.ServiceDiscovery;
import cn.yuanyuan.rpc.rpcregistryzookper.ZooKeeperServiceDiscovery;
import com.example.sampleapidemo.HelloService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@Configuration
@PropertySource("classpath:rpc.properties")
public class SampleClientDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SampleClientDemoApplication.class, args);
    }


    @Value("${rpc.registry_address}")
    private String REGISTRY_ADDRESS;

    @Bean
    public ServiceDiscovery serviceDiscovery() {
        return new ZooKeeperServiceDiscovery(REGISTRY_ADDRESS);
    }

    @Bean
    public RpcProxy rpcProxy(ServiceDiscovery serviceDiscovery) {
        return new RpcProxy(serviceDiscovery);
    }

    @Bean
    public HelloService helloService(RpcProxy rpcProxy) {
        return rpcProxy.create(HelloService.class);
    }


}
