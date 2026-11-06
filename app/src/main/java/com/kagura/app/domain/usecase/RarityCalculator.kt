package com.kagura.app.domain.usecase

import com.kagura.app.domain.model.Raridade

object RarityCalculator {
    fun calculate(score: Float, popularity: Int): Raridade {
        return when {
            score >= 8.5f && popularity >= 10000 -> Raridade.LENDARIO
            score >= 8.5f -> Raridade.EPICO
            score >= 7.5f -> Raridade.EPICO
            score >= 6.5f -> Raridade.RARO
            else -> Raridade.COMUM
        }
    }
}
