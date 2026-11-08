package com.hyunsikyoo.pleosapp.ui.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.CarDiaryColors
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.CarDiaryDimens
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.CarDiaryShadows
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.CarDiaryShapes
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.CarDiaryTypography
import com.hyunsikyoo.pleosapp.ui.designsystem.foundation.hardShadow
import com.hyunsikyoo.pleosapp.ui.designsystem.icon.CarDiaryIcons

private val CheckboxShape = RoundedCornerShape(10.dp)

/**
 * Checkbox (Figma 102:117, 11/8). 40×40, 모서리 10.
 * Off #FFFFFF + 테두리 #000 / On Main #5294FF + 테두리 #000 + 그림자 2/2, 검정 check 28.
 * List Row Trailing=Checkbox 안에서 줄의 State를 따라 바뀐다(눌림은 줄이 받는다).
 */
@Composable
fun CarDiaryCheckbox(checked: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(40.dp)
            .then(if (checked) Modifier.hardShadow(2.dp, CheckboxShape) else Modifier)
            .background(if (checked) CarDiaryColors.Main else CarDiaryColors.Card, CheckboxShape)
            .border(CarDiaryDimens.Stroke, CarDiaryColors.Line, CheckboxShape),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) CarDiaryIcon(CarDiaryIcons.Check, contentDescription = null, size = 28.dp)
    }
}

/**
 * Text Field (Figma 107:133, 11/8). 세로, 간격 10. 라벨 24 #000 50% + 입력 칸 높이 88, 좌우 28, 모서리 20.
 * Empty = 예시 글자 #000 35%, Typing = Main 3px 테두리 + 그림자 4/4 + 커서, Filled = 넣은 값.
 * 정차 중에만 쓴다(Pleos 키보드). 차에서 글자를 넣는 건 처음 한 번뿐이라는 원칙.
 */
@Composable
fun CarDiaryTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    icon: ImageVector? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    val interaction = remember { MutableInteractionSource() }
    val typing by interaction.collectIsFocusedAsState()
    Column(modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        CarDiaryText(label, style = CarDiaryTypography.LabelMedium, color = CarDiaryColors.TextSub)
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            interactionSource = interaction,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            textStyle = CarDiaryTypography.BodyLargeNormal,
            cursorBrush = SolidColor(CarDiaryColors.Line),
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { inner ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(88.dp)
                        .then(if (typing) Modifier.hardShadow(CarDiaryShadows.Soft, CarDiaryShapes.ListRow) else Modifier)
                        .background(CarDiaryColors.Card, CarDiaryShapes.ListRow)
                        .border(
                            if (typing) 3.dp else CarDiaryDimens.Stroke,
                            if (typing) CarDiaryColors.Main else CarDiaryColors.LineSoft,
                            CarDiaryShapes.ListRow,
                        )
                        .padding(horizontal = 28.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            CarDiaryText(placeholder, style = CarDiaryTypography.BodyLargeNormal, color = CarDiaryColors.TextDisabled, maxLines = 1)
                        }
                        inner()
                    }
                    if (icon != null) CarDiaryIcon(icon, contentDescription = null, size = 32.dp)
                }
            },
        )
    }
}

/**
 * Toast (Figma 110:8181, 11/8). 패딩 22/36, 간격 14, 모서리 100, #000 85%, 흰 글자 28 Bold, 아이콘 32 흰 선(기본 check).
 * 3초 뒤 사라짐, 앱 영역 아래 가운데. 보이고 숨기는 시간은 부르는 쪽이 정한다.
 */
@Composable
fun CarDiaryToast(
    label: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = CarDiaryIcons.Check,
) {
    Row(
        modifier = modifier
            .background(CarDiaryColors.Toast, CarDiaryShapes.Pill)
            .padding(horizontal = 36.dp, vertical = 22.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        if (icon != null) CarDiaryIcon(icon, contentDescription = null, size = 32.dp, tint = CarDiaryColors.Card)
        CarDiaryText(label, style = CarDiaryTypography.BodyMediumStrong, color = CarDiaryColors.Card, maxLines = 1)
    }
}
