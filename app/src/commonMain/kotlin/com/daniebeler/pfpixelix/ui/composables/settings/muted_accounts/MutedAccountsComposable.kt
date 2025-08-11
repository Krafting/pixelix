package com.daniebeler.pfpixelix.ui.composables.settings.muted_accounts

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.ui.composables.states.FullscreenEmptyStateComposable
import com.daniebeler.pfpixelix.ui.composables.states.FullscreenErrorComposable
import com.daniebeler.pfpixelix.ui.composables.states.FullscreenLoadingComposable
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.chevron_back_outline
import pixelix.app.generated.resources.muted_accounts
import pixelix.app.generated.resources.no_muted_accounts

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun MutedAccountsComposable(
    navController: NavController,
    viewModel: MutedAccountsViewModel = injectViewModel(key = "muted-accounts-key") { mutedAccountsViewModel }
) {
    Scaffold(contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top), topBar = {
        TopAppBar(title = {
            Text(
                text = stringResource(Res.string.muted_accounts),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
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
        PullToRefreshBox(
            onRefresh = { viewModel.getMutedAccounts(true) },
            isRefreshing = viewModel.mutedAccountsState.isRefreshing,
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(viewModel.mutedAccountsState.mutedAccounts, key = {
                    it.id
                }) {
                    Row {
                        CustomMutedAccountRow(
                            account = it, navController = navController, viewModel
                        )
                    }
                }
            }

            if (viewModel.mutedAccountsState.mutedAccounts.isEmpty()) {
                if (viewModel.mutedAccountsState.isLoading && !viewModel.mutedAccountsState.isRefreshing) {
                    FullscreenLoadingComposable()
                }

                if (viewModel.mutedAccountsState.error.isNotEmpty()) {
                    FullscreenErrorComposable(message = viewModel.mutedAccountsState.error)
                }

                if (!viewModel.mutedAccountsState.isLoading && viewModel.mutedAccountsState.error.isEmpty()) {
                    FullscreenEmptyStateComposable(EmptyState(heading = stringResource(Res.string.no_muted_accounts)))
                }
            }
        }

    }
}