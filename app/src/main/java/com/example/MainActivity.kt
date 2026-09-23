package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.ElayaDatabase
import com.example.data.model.UserRole
import com.example.data.repository.ElayaRepository
import com.example.ui.admin.AdminDashboard
import com.example.ui.auth.AuthScreen
import com.example.ui.customer.CustomerDashboard
import com.example.ui.owner.FlowerShopOwnerDashboard
import com.example.ui.theme.ElayaAdminTheme
import com.example.ui.theme.ElayaBackground
import com.example.ui.theme.ElayaOwnerTheme
import com.example.ui.theme.ElayaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appScope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main + kotlinx.coroutines.SupervisorJob())
        val database = ElayaDatabase.getDatabase(applicationContext, appScope)
        val repository = ElayaRepository(database, appScope)

        setContent {
            ElayaApp(repository = repository)
        }
    }
}

@Composable
fun ElayaApp(repository: ElayaRepository) {
    val currentUser by repository.currentUser.collectAsStateWithLifecycle()

    val currentRole = currentUser?.let { UserRole.fromRoleName(it.role) }

    when (currentRole) {
        UserRole.CUSTOMER -> {
            ElayaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ElayaBackground
                ) {
                    CustomerDashboard(
                        repository = repository,
                        onLogout = { repository.logout() },
                        onSwitchRole = { newRole ->
                            // Fast switch
                        }
                    )
                }
            }
        }

        UserRole.FLOWER_OWNER -> {
            ElayaOwnerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ElayaBackground
                ) {
                    FlowerShopOwnerDashboard(
                        repository = repository,
                        onLogout = { repository.logout() },
                        onSwitchRole = { newRole ->
                            // Fast switch
                        }
                    )
                }
            }
        }

        UserRole.ADMIN -> {
            ElayaAdminTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ElayaBackground
                ) {
                    AdminDashboard(
                        repository = repository,
                        onLogout = { repository.logout() },
                        onSwitchRole = { newRole ->
                            // Fast switch
                        }
                    )
                }
            }
        }

        null -> {
            ElayaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ElayaBackground
                ) {
                    AuthScreen(
                        repository = repository,
                        onLoginSuccess = { role ->
                            // Role is updated in repository.currentUser
                        }
                    )
                }
            }
        }
    }
}
