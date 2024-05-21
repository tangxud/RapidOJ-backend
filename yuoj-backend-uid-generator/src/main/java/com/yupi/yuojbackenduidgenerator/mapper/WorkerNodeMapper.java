package com.yupi.yuojbackenduidgenerator.mapper;

import com.yupi.yuojbackenduidgenerator.domain.WorkerNode;
import org.apache.ibatis.annotations.Param;
import org.mapstruct.Mapper;

@Mapper
public interface WorkerNodeMapper {

    int addWorkerNode(WorkerNode workerNodeEntity);


    WorkerNode getWorkerNodeByHostPort(@Param("host") String host, @Param("port") String port);

}