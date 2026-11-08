package com.hyunsikyoo.pleosapp.ui.designsystem.foundation

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.Modifier

/** 눌림 표시 없음. 디자인 시스템 컴포넌트가 각자 눌림 모양을 그린다 */
object NoIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode = object : Modifier.Node() {}
    override fun equals(other: Any?): Boolean = other === this
    override fun hashCode(): Int = System.identityHashCode(this)
}
