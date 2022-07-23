package com.miguelangelboti.happycontacts.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.miguelangelboti.happycontacts.R
import com.miguelangelboti.happycontacts.data.model.Contact
import com.miguelangelboti.happycontacts.ui.theme.HappyContactsTheme
import com.miguelangelboti.happycontacts.ui.utils.dayAsInt
import com.miguelangelboti.happycontacts.ui.utils.initials
import com.miguelangelboti.happycontacts.ui.utils.monthAsString
import java.util.Date

@Composable
fun ContactItem(modifier: Modifier = Modifier, contact: Contact) {
    Row(
        modifier
            .padding(10.dp)
            .fillMaxWidth()
    ) {
        ContactImage(contact = contact)
        Column {
            Text(
                modifier = Modifier.padding(start = 10.dp),
                text = contact.name
            )
            Text(
                modifier = Modifier.padding(start = 10.dp),
                fontStyle = FontStyle.Italic,
                text = stringResource(id = R.string.date, contact.birthday.dayAsInt(), contact.birthday.monthAsString())
            )
        }
    }
}

@Composable
fun ContactImage(contact: Contact) {
    if (contact.imageUrl != null) {
        Image(
            modifier = Modifier.size(52.dp),
            painter = rememberImagePainter(
                data = contact.imageUrl,
                builder = {
                    transformations(CircleCropTransformation())
                }
            ),
            contentDescription = null
        )
    } else {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = contact.name.initials(2),
                fontSize = 20.sp,
                color = Color.White
            )
        }
    }
}

@Preview
@Composable
fun ContactItemPreview() {
    HappyContactsTheme {
        ContactItem(contact = Contact("Miguel Ángel", Date(), null))
    }
}

@Preview
@Composable
fun ContactItemPreviewDark() {
    HappyContactsTheme(isDarkTheme = true) {
        ContactItem(contact = Contact("Miguel Ángel", Date(), null))
    }
}
