package com.asp.data.mapper

interface Mapper<Response, Entity, out Model> {
    fun mapResponse(input: Response): Entity

    fun mapEntity(input: Entity): Model
}