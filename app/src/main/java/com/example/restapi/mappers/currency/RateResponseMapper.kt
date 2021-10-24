package com.example.restapi.mappers.currency

import com.example.restapi.data.dto.currency.RateResponse
import com.example.restapi.data.entities.currency.Rate
import com.example.restapi.mappers.Mapper

class RateResponseMapper : Mapper<RateResponse, Rate> {
    override fun map(from: RateResponse): Rate {
        return Rate(
            curID = from.curID ?: -1,
            curAbbreviation = from.curAbbreviation.orEmpty(),
            curName = from.curName.orEmpty(),
            curOfficialRate = from.curOfficialRate ?: 0.0,
            curScale = from.curScale ?: -1,
            date = from.date.orEmpty()
        )
    }
}