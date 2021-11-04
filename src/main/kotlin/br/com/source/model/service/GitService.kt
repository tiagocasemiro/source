package br.com.source.model.service

import br.com.source.model.domain.RemoteRepository
import br.com.source.model.util.Message
import br.com.source.model.util.errorOn
import br.com.source.view.model.Branch
import br.com.source.view.model.Stash
import br.com.source.view.model.Tag
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand.ListMode.REMOTE
import org.eclipse.jgit.lib.ProgressMonitor
import org.eclipse.jgit.lib.Ref

class GitService(private val git: Git) {
    fun clone(remoteRepository: RemoteRepository): Message<Unit> {

        println("Cloning from " + remoteRepository.url + " to " + remoteRepository.localRepository.workDir)

        val result = Git.cloneRepository()
            .setURI(remoteRepository.url)
            .setDirectory(remoteRepository.localRepository.fileWorkDir())
            .setProgressMonitor(object : ProgressMonitor {
                override fun start(totalTasks: Int) {
                    println("Starting work on $totalTasks tasks")
                }

                override fun beginTask(title: String, totalWork: Int) {
                    println("Start $title: $totalWork")
                }

                override fun update(completed: Int) {
                    print("$completed-")
                }

                override fun endTask() {
                    println("Done")
                }

                override fun isCancelled(): Boolean {
                    return false
                }
            }).call()
        println("Having repository: " + result.repository.directory);

        return Message.Success<Unit>()
    }

    fun localBranches(): Message<List<Branch>> {
        return try {
            val refs = git.branchList().call()
            Message.Success(obj = refs.map {
                Branch(fullName = it.name, isCurrent = it.name == git.repository.fullBranch)
            })
        } catch (e: Exception) {
            Message.Error(errorOn("Load local branches"))
        }
    }

    fun remoteBranches(): Message<List<Branch>> {
        val refs = git.branchList().setListMode(REMOTE).call()
        return try {
            Message.Success(obj = refs.map {
                Branch(fullName = it.name)
            })
        } catch (e: Exception) {
            Message.Error(errorOn("Load remote branches"))
        }
    }

    fun tags(): List<Tag> {
        val refs: List<Ref> =  git.tagList().call()
        val tags = refs.map {
            Tag(it.name.split("/").last())
        }

        return tags
    }

    fun stashs(): List<Stash> {
        val listRevCommit = git.stashList().call()

        return listRevCommit.mapIndexed { index, revCommit ->
            val name = revCommit.shortMessage.split(":").takeIf {it.size > 1}?.get(1)?.trimStart()?.trimEnd()
                ?.split(" ").takeIf { it != null && it.size > 1}?.toMutableList().apply {this?.removeFirst()}
                ?.joinToString(separator = " ")?: "stash@{$index}"

            Stash(name)
        }
    }

    fun deleteLocalBranch(name: String): Message<Unit> {
        return try {
            val list = git.branchDelete()
                .setBranchNames(name)
                .setForce(true)
                .call();

           if(list.isEmpty())
               return Message.Error()

            Message.Success()
        } catch (e: Exception) {
            Message.Error("Delete local branch")
        }
    }

    fun deleteRemoteBranch(name: String) {
        val list = git.branchDelete()
            .setBranchNames("origin/$name")
            .setForce(true)
            .call();

        list.forEach {
            println(it)
        }
    }

    fun checkoutLocalBranch(name: String): Message<Unit> {
        return try {
            git.checkout().setName(name).call()
            Message.Success()
        } catch (e: Exception) {
            Message.Error("Checkout local branch")
        }
    }
}
