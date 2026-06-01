package com.assembly.core.operations;

public interface OperationInterface
{

    void operationSuccess(OperationQuery query, Object response);

    void operationError(OperationQuery query, Object response);
}
