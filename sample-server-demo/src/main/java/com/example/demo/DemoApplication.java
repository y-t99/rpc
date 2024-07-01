package com.example.demo;

import cn.yuanyuan.rpc.rpcregistry.ServiceRegistry;
import cn.yuanyuan.rpc.rpcregistryzookper.ZooKeeperServiceRegistry;
import cn.yuanyuan.rpc.rpcserver.RpcServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@Configuration
@PropertySource("classpath:rpc.properties")
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Value("${rpc.service_address}")
    private String SERVICE_ADDRESS;

    @Value("${rpc.registry_address}")
    private String REGISTRY_ADDRESS;

    @Bean
    public RpcServer rpcServer(ServiceRegistry serviceRegistry) {
        return new RpcServer(SERVICE_ADDRESS, serviceRegistry);
    }

    @Bean
    public ServiceRegistry serviceRegistry() {
        return new ZooKeeperServiceRegistry(REGISTRY_ADDRESS);
    }
}
