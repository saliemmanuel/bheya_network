package com.example.bheya_network.core.model.connection


/**
 * Cell is primary for data or voice communication with outer world
 */
class PrimaryConnection : IConnection {

    override fun toString(): String {
        return "PrimaryConnection()"
    }

    override fun equals(other: Any?) = other?.javaClass == javaClass
    override fun hashCode() = toString().hashCode()
}