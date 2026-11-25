package id.pamtirtakemuning.watermeter.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.pamtirtakemuning.watermeter.R
import id.pamtirtakemuning.watermeter.Tab
import id.pamtirtakemuning.watermeter.ui.theme.Ice
import id.pamtirtakemuning.watermeter.ui.theme.Muted
import id.pamtirtakemuning.watermeter.ui.theme.Navy
import id.pamtirtakemuning.watermeter.ui.theme.navy

@Composable
fun BottomNav(active: Tab, onChange: (Tab) -> Unit, modifier: Modifier = Modifier) {
    val items = listOf(
        Triple(Tab.Tugas, "Tugas", R.drawable.ic_clipboard_list),
        Triple(Tab.Riwayat, "Riwayat", R.drawable.ic_history),
        Triple(Tab.Profil, "Profile", R.drawable.ic_user_round),
    )
    val shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    Row(
        modifier.fillMaxWidth()
            .shadow(12.dp, shape, ambientColor = navy(0.12f), spotColor = navy(0.12f))
            .background(Color.White, shape)
            .navigationBarsPadding()
            .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        items.forEach { (tab, label, icon) ->
            val on = tab == active
            Column(
                Modifier.width(96.dp).clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onChange(tab) },
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Box(Modifier.width(56.dp).height(32.dp).background(if (on) Ice else Color.Transparent, CircleShape), contentAlignment = Alignment.Center) {
                    Ic(icon, 22.dp, tint = if (on) Navy else Muted)
                }
                PText(label, size = 11, weight = FontWeight.SemiBold, color = if (on) Navy else Muted, lineHeight = 1.2f)
            }
        }
    }
}
