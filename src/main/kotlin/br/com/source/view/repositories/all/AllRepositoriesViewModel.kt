package br.com.source.view.repositories.all

import br.com.source.model.database.LocalRepositoryDatabase
import br.com.source.model.domain.LocalRepository
import br.com.source.model.process.runCommand
import br.com.source.model.service.GitService
import br.com.source.model.util.emptyString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class AllRepositoriesViewModel(private val localRepositoryDatabase: LocalRepositoryDatabase) {
    private val coroutine = CoroutineScope(Dispatchers.Main)
    private val _repositories = MutableStateFlow<List<LocalRepository>>(emptyList())
    val repositories: StateFlow<List<LocalRepository>> = _repositories
    private val _status = MutableStateFlow(emptyString())
    val status: StateFlow<String> = _status

    init {
        all()
    }

    fun checkRepository(localRepository: LocalRepository, onOk: () -> Unit) {
        coroutine.async {
            GitService(localRepository).checkRepository().onSuccess {
                onOk()
            }
        }
    }

    fun status(workDir: String) {
        coroutine.launch {
            _status.value = runCommand("git status", File(workDir))
        }
    }

    fun all() {
        coroutine.async {
            _repositories.value = localRepositoryDatabase.all()
            _repositories.value.firstOrNull()?.let {
                GitService(it).checkRepository()
            }
        }
    }

    fun delete(localRepository: LocalRepository) {
        coroutine.async {
            localRepositoryDatabase.delete(localRepository)
            _repositories.value = localRepositoryDatabase.all()
            _status.value = emptyString()
        }
    }
}