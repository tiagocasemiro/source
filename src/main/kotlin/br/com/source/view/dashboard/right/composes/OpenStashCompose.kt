package br.com.source.view.dashboard.right.composes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.source.model.util.emptyString
import br.com.source.view.common.*
import br.com.source.view.model.Change
import br.com.source.view.model.Diff
import br.com.source.view.model.Line
import org.eclipse.jgit.diff.DiffEntry

@Composable
fun OpenStashCompose(diffs: List<Diff>) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        LazyColumn(
            Modifier.fillMaxSize()
        ) {
            items(diffs) { diff ->
                FileChange(diff)
            }
            item {
                Spacer(Modifier.height(1.dp).fillMaxWidth().background(itemRepositoryBackground))
                Spacer(Modifier.height(20.dp).fillMaxWidth())
            }
        }
    }
}

@Composable
fun FileChange(diff: Diff) {
    Column {
        Row(
            Modifier.height(32.dp).fillMaxWidth().background(cardBackgroundColor),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val resourcePath = when(diff.changeType) {
                DiffEntry.ChangeType.ADD -> "images/diff/ic-add-file.svg"
                DiffEntry.ChangeType.COPY -> "images/diff/ic-copy-file.svg"
                DiffEntry.ChangeType.DELETE -> "images/diff/ic-remove-file.svg"
                DiffEntry.ChangeType.MODIFY -> "images/diff/ic-modify-file.svg"
                DiffEntry.ChangeType.RENAME -> "images/diff/ic-rename-file.svg"
            }
            Spacer(Modifier.size(10.dp))
            Icon(
                painterResource(resourcePath),
                contentDescription = "Indication of expanded card",
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = diff.fileName,
                modifier = Modifier.padding(start = 10.dp),
                fontFamily = Fonts.roboto(),
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                color = itemRepositoryText,
                textAlign = TextAlign.Left
            )
        }
        diff.changes.forEachIndexed { index, change ->
            ChangeCompose(change = change, index = index)
        }
    }
}

@Composable
fun ChangeCompose(change: Change, index: Int) {
    Column {
        Spacer(Modifier.height(1.dp).fillMaxWidth().background(itemRepositoryBackground))
        Column(
            Modifier.background(dialogBackgroundColor).fillMaxWidth().height(25.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Change ${index + 1}: lines, from ${change.positionOfChanges.startNew} to ${change.positionOfChanges.startNew + change.positionOfChanges.totalNew - 1}",
                modifier = Modifier.padding(start = 70.dp),
                fontFamily = Fonts.roboto(),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = itemRepositoryText,
                textAlign = TextAlign.Left
            )
        }
        Spacer(Modifier.height(1.dp).fillMaxWidth().background(itemRepositoryBackground))
        change.lines.forEach { line ->
            val background = when(line) {
                is Line.Add -> Color(220,235,220)
                is Line.Remove -> Color(235,220,220)
                else -> Color.Transparent
            }
            Row(
                Modifier.height(25.dp).background(background).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row (
                    modifier = Modifier.background(dialogBackgroundColor),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Spacer(Modifier.width(1.dp).fillMaxHeight().background(itemRepositoryBackground))
                    Text(
                        text = if(line.numberOld == null) emptyString() else line.numberOld.toString(),
                        modifier = Modifier.width(25.dp),
                        fontFamily = Fonts.roboto(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = itemRepositoryText,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.width(1.dp).fillMaxHeight().background(itemRepositoryBackground))
                    Text(
                        text = if(line.numberNew == null) emptyString() else line.numberNew.toString(),
                        modifier = Modifier.width(25.dp),
                        fontFamily = Fonts.roboto(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = itemRepositoryText,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.width(1.dp).fillMaxHeight().background(itemRepositoryBackground))
                }
                val textColor = when(line) {
                    is Line.Add -> Color(0,150,0)
                    is Line.Remove -> Color(150,0,0)
                    else -> itemRepositoryText
                }
                Text(
                    text = line.content,
                    modifier = Modifier,
                    fontFamily = Fonts.roboto(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = textColor,
                    textAlign = TextAlign.Left,
                )
            }
        }
    }
}