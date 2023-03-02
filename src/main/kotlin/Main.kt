import java.io.File
import java.lang.Exception

const val NUMBER_OF_CHILDREN_AND_HOSTS = 100

open class Base(
    val name: String,
    var preference: List<Base>,
    var selected: Base?
) {
    fun print() {
        println("$name: ${preference.map { it.name }}")
    }

    fun printSelected() {
        println("$name -> ${selected!!.name}")
    }
}

class Child(name: String, preference: List<Base>, selected: Base?) : Base(name, preference, selected)
class Host(name: String, preference: List<Base>, selected: Base?) : Base(name, preference, selected)

fun readNames(): MutableList<String> {
    return File("names.txt").readLines(charset = Charsets.UTF_8).toMutableList()
}

fun main() {
    val names = readNames()

    val hosts = MutableList(NUMBER_OF_CHILDREN_AND_HOSTS) {
        val name = names.random()
        names.remove(name)
        Host(name, emptyList(), null)
    }

    val children = MutableList(NUMBER_OF_CHILDREN_AND_HOSTS) {
        val name = names.random()
        names.remove(name)
        Child(name, hosts.shuffled(), null)
    }

    println("Hosts: ")
    hosts.forEach {
        it.preference = children.shuffled()
        it.print()
    }

    println("Children: ")
    children.forEach {
        it.print()
    }

    while(!children.all { it.selected != null }) {
        val child = children.find { it.selected == null } ?: throw Exception()

        @Suppress("LoopWithTooManyJumpStatements")
        for (preferredHost in child.preference) {
            if(preferredHost.selected == null) {
                child.selected = preferredHost
                preferredHost.selected = child
                break
            } else if(preferredHost.preference.indexOf(preferredHost.selected) > preferredHost.preference.indexOf(child)) {
                preferredHost.selected!!.selected = null
                child.selected = preferredHost
                preferredHost.selected = child
                break
            }
        }
    }

    println("\nSelection:")
    children.forEach {
        it.printSelected()
    }

}
