package tasklist

import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.datetime.*
import java.io.File

class TaskUnit() {
//    var unitDate: LocalDate? = null
    var unitDateInString = ""
//    var unitTime: LocalDateTime? = null
    var unitTimeToString = ""
    var unitTaskPriority: String = ""
    var unitDueTag: Char = ' '
    var unitTaskLines = mutableListOf<String>()

    fun setDateAndTag(){
        var date: LocalDate? = null
        do {
            println("Input the date (yyyy-mm-dd):")
            val inputDate = readln()
            var checkingDateOk: Boolean

            try {
                val year = inputDate.substringBefore('-').toInt()
                val restSubString = inputDate.substringAfter('-')
                val month = restSubString.substringBefore('-').toInt()
                val day = inputDate.substringAfterLast('-').toInt()
                date = LocalDate(year, month, day)
                checkingDateOk = true
            }
            catch (e: Exception) {
                checkingDateOk = false
                println("The input date is invalid")
                continue
            }
        } while (!checkingDateOk)
        unitDateInString = date.toString()
//        unitDate = date

        //        set tag
        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+0")).date
        val numberOfDays = currentDate.daysUntil(date!!) // unitDate!!
        unitDueTag = when {
            numberOfDays == 0 -> 'T'
            numberOfDays > 0 -> 'I'
            else -> 'O'
        }
    }

    fun setTime(unitDate: String){
        val year = unitDate.substring(0,4).toInt()
        val month = unitDate.substring(5,7).toInt()
        val dayOfMonth = unitDate.substring(8).toInt()
        var time: LocalDateTime? = null

        do {
            println("Input the time (hh:mm):")
            val inputTime = readln()
            var checkingDateOk: Boolean

            try {
                time = LocalDateTime(year, month, dayOfMonth, inputTime.substringBefore(':').toInt(),inputTime.substringAfter(':').toInt())
                // unitDate!!.year, unitDate!!.month, unitDate!!.dayOfMonth
                checkingDateOk = true
            }
            catch (e: Exception) {
                checkingDateOk = false
                println("The input time is invalid")
                continue
            }
        } while (!checkingDateOk)
//        unitTime = time
        unitTimeToString = time.toString().substringAfter('T')

    }

    fun setPriority() {
        val priorityRegex = Regex("C|H|N|L")
        var taskPriority = ""
        do {
            println("Input the task priority (C, H, N, L):")
            taskPriority = readln().uppercase()
        } while (!taskPriority.matches(priorityRegex))
        unitTaskPriority = taskPriority

    }


    fun addNewTask(){
        println("Input a new task (enter a blank line to end):")
    do{
        val readLine = readln().trim()
        if (readLine == "" && unitTaskLines.isEmpty()) {
            println("The task is blank")
        } else if (readLine.isNotEmpty()) {
            unitTaskLines.add(readLine)
        }
    } while (readLine != "")
    }

}

fun main() {

    var taskList = mutableListOf<TaskUnit>()

    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val type = Types.newParameterizedType(MutableList::class.java, TaskUnit::class.java)
    val taskAdapter = moshi.adapter<MutableList<TaskUnit>>(type)

    val fileName = File("tasklist.json")
    if (fileName.exists()) {
        val jsonString = fileName.readText()
        taskList = taskAdapter.fromJson(jsonString)!!
    }


    val regex = Regex("add|print|end|delete|edit")
    var readAction = ""

    while (readAction != "end") {
        println("Input an action (add, print, edit, delete, end): ")
        readAction = readln().lowercase()
        if (!readAction.matches(regex)) {
            println("The input action is invalid")
            continue
        }
        when (readAction) {
            "add" -> {
                val newTask = TaskUnit()
                newTask.setPriority()
                newTask.setDateAndTag()
                newTask.setTime(newTask.unitDateInString)
                newTask.addNewTask()
                if (newTask.unitTaskLines.isNotEmpty()) taskList.add(newTask)

            }
            "delete" -> deleteTask(taskList)
            "edit" -> editTask(taskList)

            "print" -> {
                printFullTasksList(taskList)
            }
        }

    }

    fileName.writeText(taskAdapter.toJson(taskList))

    println("Tasklist exiting!")

}

fun editTask(taskList: MutableList<TaskUnit>) {
    var taskNumberToEdit = 0
    if (taskList.isEmpty()) println("No tasks have been input")
    else {
        printFullTasksList(taskList)
        do {
            println("Input the task number (1-${taskList.size}):")
            try {
                taskNumberToEdit = readln().toInt()
            } catch (e: Exception) {
              println("Invalid task number")
                continue
            }
            if (taskNumberToEdit !in 1..taskList.size) println("Invalid task number")
        } while (taskNumberToEdit !in 1..taskList.size)


        do {
            var checkContinue = true
            println("Input a field to edit (priority, date, time, task):")
            val fieldToEdit = readln()
            when (fieldToEdit) {
                "priority" -> taskList[taskNumberToEdit-1].setPriority()
                "date" -> taskList[taskNumberToEdit-1].setDateAndTag()
                "time" -> taskList[taskNumberToEdit-1].setTime(taskList[taskNumberToEdit-1].unitDateInString)
                "task" -> {
                    taskList[taskNumberToEdit-1].unitTaskLines.clear()
                    taskList[taskNumberToEdit-1].addNewTask()
                }
                else -> {
                    println("Invalid field")
                    checkContinue = false
                    continue
                }

            }
        } while (!checkContinue)
        println("The task is changed")
    }
}

fun deleteTask(taskList: MutableList<TaskUnit>) {
    var taskNumberToDelete = 0
    if (taskList.isEmpty()) println("No tasks have been input")
    else {
        printFullTasksList(taskList)
        do {
            println("Input the task number (1-${taskList.size}):")
            try {
                taskNumberToDelete = readln().toInt()
            } catch (e: Exception) {
                println("Invalid task number")
                continue
            }
            if (taskNumberToDelete !in 1..taskList.size) println("Invalid task number")
        } while (taskNumberToDelete !in 1..taskList.size)
        taskList.removeAt(taskNumberToDelete-1)
        println("The task is deleted")
    }
}

fun printFullTasksList(list: MutableList<TaskUnit>) {
    if (list.isEmpty()) {
        println("No tasks have been input")
    } else {
        println(
            """
            +----+------------+-------+---+---+--------------------------------------------+
            | N  |    Date    | Time  | P | D |                   Task                     |
            +----+------------+-------+---+---+--------------------------------------------+
        """.trimIndent()
        )
        for (i in list.indices) {
            if (i < 9) print("| ${i + 1}  |") else print("| ${i + 1} |")
            print(" ${list[i].unitDateInString} |") // unitDate
            print(" ${list[i].unitTimeToString} |") // unitTime.toString().substringAfter('T')
            when (list[i].unitTaskPriority) {
                "C" -> print(" \u001B[101m \u001B[0m |")
                "H" -> print(" \u001B[103m \u001B[0m |")
                "N" -> print(" \u001B[102m \u001B[0m |")
                "L" -> print(" \u001B[104m \u001B[0m |")
            }
            when (list[i].unitDueTag) {
                'I' -> print(" \u001B[102m \u001B[0m |")
                'T' -> print(" \u001B[103m \u001B[0m |")
                'O' -> print(" \u001B[101m \u001B[0m |")
            }
            list[i].unitTaskLines.forEach {
                if (list[i].unitTaskLines.size == 1) {
                    if (it.length < 44) {
                        print(it)
                        repeat(44 - it.length) { print(" ") }
                        println("|")
                    } else printLongTask(it)
                } else {
                    if (it.length <= 44) {
                        print(it)
                        repeat(44 - it.length) { print(" ") }
                        println("|")
                        if (list[i].unitTaskLines.indexOf(it) != list[i].unitTaskLines.size - 1) print("|    |            |       |   |   |")
                    }
                    else {
                        printLongTask(it)
                        if (list[i].unitTaskLines.indexOf(it) != list[i].unitTaskLines.size - 1) print("|    |            |       |   |   |")

                    }
                }

            }
            println("+----+------------+-------+---+---+--------------------------------------------+")
        }
    }
}

fun printLongTask(it: String) {
    var restLongString = it
    while (restLongString.length > 44) {
        println("${restLongString.substring(0,44)}|")
        restLongString = restLongString.substring(44)
        print("|    |            |       |   |   |")
    }
    if (restLongString.isNotEmpty()) {
        print(restLongString)
        repeat(44 - restLongString.length) { print(" ") }
        println("|")
    }


}

