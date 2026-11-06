package com.kagura.app.domain.usecase

import com.kagura.app.domain.model.ItemStatus
import com.kagura.app.domain.model.MediaItem
import com.kagura.app.domain.model.MediaType
import com.kagura.app.domain.model.Raridade
import javax.inject.Inject

class FilterDexUseCase @Inject constructor() {
    operator fun invoke(items: List<MediaItem>, type: MediaType, rarity: Raridade?): List<MediaItem> =
        items.filter { it.type == type && (rarity == null || it.rarity == rarity) }
            .sortedWith(compareByDescending<MediaItem> { it.rarity.ordinal }.thenBy { it.title })

    fun progress(items: List<MediaItem>, type: MediaType): Float {
        val l = items.filter { it.type == type }
        return if (l.isEmpty()) 0f else l.count { it.status == ItemStatus.COMPLETED } / l.size.toFloat()
    }
}
