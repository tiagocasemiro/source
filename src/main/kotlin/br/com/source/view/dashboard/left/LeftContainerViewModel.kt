package br.com.source.view.dashboard.left

import br.com.source.model.domain.LocalRepository
import br.com.source.model.service.GitService
import br.com.source.model.util.Message
import br.com.source.view.model.Branch
import br.com.source.view.model.Stash
import br.com.source.view.model.Tag
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.get


class LeftContainerViewModel(private val localRepository: LocalRepository) {

    private val gitService: GitService = get(GitService::class.java) { parametersOf(localRepository.fileWorkDir()) }

    fun localBranches(): Message<List<Branch>> {
        return gitService.localBranches()
    }

    fun remoteBranches(): Message<List<Branch>> {
        return gitService.remoteBranches()
    }

    fun tags(): Message<List<Tag>> {
        return gitService.tags()
    }

    fun stashs(): Message<List<Stash>> {
        return gitService.stashs()
    }

    fun deleteLocalBranch(branch: Branch): Message<Unit> {
       return gitService.deleteLocalBranch(branch.clearName)
    }

    fun deleteRemoteBranch(branch: Branch): Message<Unit> {
        return gitService.deleteRemoteBranch(branch.clearName)
    }

    fun checkoutLocalBranch(branch: Branch): Message<Unit> {
        return gitService.checkoutLocalBranch(branch.clearName)
    }

    fun checkoutRemoteBranch(branch: Branch): Message<Unit> {
        return gitService.checkoutRemoteBranch(branch.clearName)
    }

    fun isLocalBranch(branch: Branch, branches: List<Branch>): Boolean {
        for(it in branches)
            if(it.clearName == branch.clearName)
                return true

        return false
    }

    fun checkoutTag(tag: Tag): Message<String> {
        return gitService.checkoutTag(tag.objectId)
    }

    fun delete(tag: Tag): Message<String> {
        return gitService.deleteTag(tag.name)
    }

    fun applyStash(stash: Stash): Message<Unit> {
        return gitService.applyStash(stash.originalName)
    }

    fun delete(stash: Stash): Message<Unit> {
        return gitService.deleteStash(stash.index)
    }
}