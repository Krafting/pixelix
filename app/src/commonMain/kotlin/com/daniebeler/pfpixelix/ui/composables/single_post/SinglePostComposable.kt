package com.daniebeler.pfpixelix.ui.composables.single_post

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.post.PostComposable
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposable
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable
import com.daniebeler.pfpixelix.ui.navigation.Destination
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.by
import pixelix.app.generated.resources.post

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SinglePostComposable(
    navController: NavController,
    postId: String,
    refresh: Boolean,
    openReplies: Boolean,
    viewModel: SinglePostViewModel = injectViewModel(key = "single-post$postId") { singlePostViewModel }
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.getPost(postId)
    }

    LaunchedEffect(refresh) {
        if (refresh) {
            viewModel.postState = SinglePostState()
            viewModel.getPost(postId)
        }
    }

    Scaffold(contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top), topBar = {
        TopAppBar(title = {
            Column {
                Text(
                    text = stringResource(Res.string.post),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = stringResource(
                        Res.string.by, (viewModel.postState.post?.account?.username ?: "")
                    ), fontSize = 12.sp, lineHeight = 6.sp
                )
            }
        }, navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = ""
                )
            }
        })
    }) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues).fillMaxSize()
        ) {
            Column(modifier = Modifier.verticalScroll(scrollState)) {
                if (viewModel.postState.post != null) {
                    PostComposable(
                        viewModel.postState.post!!, navController, postGetsDeleted = {
                        navController.navigate(Destination.OwnProfile) {
                            popUpTo(0) { inclusive = true }
                        }
                    }, setZindex = { }, openReplies
                    )
                }
            }

            LoadingComposable(isLoading = viewModel.postState.isLoading)
            ErrorComposable(message = viewModel.postState.error)
        }
    }
}