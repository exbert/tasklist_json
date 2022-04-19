package tasklist

import kotlinx.datetime.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import java.io.File
import java.time.LocalDate
import java.time.LocalTime


@Serializable
class taskObject {

    var currentTasks: MutableList<String> = mutableListOf()
    var taskPriority: String = ""
    var taskPriorityC: String = ""
    var taskDate: kotlinx.datetime.LocalDate = kotlinx.datetime.LocalDate(2022,3,15)
    var taskTempDate = kotlinx.datetime.LocalDate(taskDate.year,taskDate.monthNumber,taskDate.dayOfMonth)
    var taskTime: MutableList<Int> = mutableListOf(22,22)
    var taskDateTime : LocalDateTime = LocalDateTime(taskDate.year, taskDate.monthNumber, taskDate.dayOfMonth, taskTime[0], taskTime[1])
    var dueTag: String = ""
    var dueTagC: String = ""
    var dueDiff: Int = 0

    init {
        //println("Creating new TaskObject")
    }

    fun calculateDate(): Int {
        var currentDate = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+0")).date
        return currentDate.daysUntil(taskTempDate)
    }
    fun dueDateCalculate() {
        val tempDueDiff: Int
        tempDueDiff = calculateDate()
        dueDiff = tempDueDiff

        when  {
            tempDueDiff == 0 -> {
                dueTagC = "\u001B[103m \u001B[0m"
                dueTag = "T"
            }
            tempDueDiff > 0 -> {
                dueTagC = "\u001B[102m \u001B[0m"
                dueTag = "I"
            }
            tempDueDiff < 0 -> {
                dueTagC = "\u001B[101m \u001B[0m"
                dueTag = "O"
            }
        }
    }

    override fun toString(): String {
        return "taskObject(currentTasks=$currentTasks, taskPriority='$taskPriority', taskPriorityC='$taskPriorityC', taskDate=$taskDate, taskTempDate=$taskTempDate, taskTime=$taskTime, taskDateTime=$taskDateTime, dueTag='$dueTag', dueTagC='$dueTagC', dueDiff=$dueDiff)"
    }
}
var taskList = mutableListOf<taskObject>()
val jsonFile = File("tasklist.json")


fun main() {
    // write your code here

    menuDisplay(taskList)
}

fun menuDisplay(taskList: MutableList<taskObject>) {
    var checkpoint = false

    while (!checkpoint) {

        println("Input an action (add, print, edit, delete, end):")

        when (readln()) {
            "add" -> {
                addTasks()
            }
            "print" -> {
                printTasks()
            }
            "end" -> {
                println("Tasklist exiting!")
                saveFile()
                checkpoint = true
            }
            "edit" -> {
                if (taskList.isEmpty()) {
                    println("No tasks have been input")
                } else editTaskControl()
            }
            "delete" -> {
                if (taskList.isEmpty()) {
                    println("No tasks have been input")
                } else deleteTask()
            }
            else -> println("The input action is invalid")
        }

    }
}

fun saveFile() {
    val toSaveJsonArray = createJsonList()
    if (jsonFile.exists()) {
        jsonFile.appendText(toSaveJsonArray.toString())
    } else {
        jsonFile.writeText(toSaveJsonArray.toString())
    }
}

fun createJsonList(): MutableList<String> {
    val tempJsonList = mutableListOf<String>()

    for (element in taskList.indices) {
        tempJsonList.add(Json.encodeToString(taskList[element]))
    }
    return tempJsonList
}


fun deleteTask() {
    var checkpoint = false
    printTasks()

    while (!checkpoint) {
        println("Input the task number (1-${taskList.size}):")
        try {
            val tempTaskID = readln().toInt()
            if (tempTaskID <= taskList.size) {
                taskList.removeAt(tempTaskID - 1)
                println("The task is deleted")
                checkpoint = true
            } else println("Invalid task number")
        }
        catch (e: Exception) {
            println("Invalid task number")
        }
    }
}

fun editTaskControl() {
    var checkpoint = false
    printTasks()

    while (!checkpoint) {
        println("Input the task number (1-${taskList.size}):")
        try {
            val tempTaskID = readln().toInt()
            if (tempTaskID <= taskList.size  && tempTaskID >= 1) {
                editTaskID(tempTaskID - 1)
                checkpoint = true
            } else println("Invalid task number")
        }
        catch (e: Exception) {
            println("Invalid task number")
        }
    }
}

fun editTaskID( tempTaskID: Int) {

    var checkpoint = false

    while (!checkpoint) {
        println("Input a field to edit (priority, date, time, task):")
        val tempCommand = readln().lowercase()
        when (tempCommand) {
            "priority" -> {
                checkpoint = setTaskPriority(tempTaskID)
            }
            "date" -> {
                checkpoint = setTaskDate(tempTaskID)
            }
            "time" -> {
                checkpoint = setTaskTime(tempTaskID)
            }
            "task" -> {
                setTaskTask(tempTaskID)
                checkpoint = true
            }
            else -> println("Invalid field")
        }
    }
}

fun setTaskTask( tempTaskID: Int) {
    var input: String

    println("Input a new task (enter a blank line to end):")

    while (true) {
        input = readLine().toString().trimIndent()
        if (input.isNullOrEmpty()) {
            break
        } else {
            taskList[tempTaskID].currentTasks.clear()
            taskList[tempTaskID].currentTasks.add(input.trimIndent())
        }
    }
    println("The task is changed")
}

fun setTaskTime(tempTaskID: Int): Boolean {
    var checkpoint = false
    var tempTimeInput: MutableList<Int> = mutableListOf(0,0)
    var tempCheckTime: LocalTime = LocalTime.of(22,22)
    var tempYear = 0
    var tempMonth = 0
    var tempDay = 0

    while (!checkpoint) {
        println("Input the time (hh:mm):")

        try {
            tempTimeInput = readln().split(":").map { it.toInt() }.toMutableList()
            tempCheckTime = LocalTime.of(tempTimeInput[0], tempTimeInput[1])
            checkpoint = true
        }
        catch (e: Exception) {
            println("The input time is invalid")
        }
    }
    taskList[tempTaskID].taskTime[0] = tempTimeInput[0]
    taskList[tempTaskID].taskTime[1] = tempTimeInput[1]
    tempYear = taskList[tempTaskID].taskDateTime.year
    tempMonth = taskList[tempTaskID].taskDateTime.monthNumber
    tempDay = taskList[tempTaskID].taskDateTime.dayOfMonth
    taskList[tempTaskID].taskDateTime = LocalDateTime(tempYear, tempMonth, tempDay, tempTimeInput[0], tempTimeInput[1])
    println("The task is changed")
    return checkpoint
}

fun setTaskDate( tempTaskID: Int): Boolean {
    var checkpoint = false
    var tempDateInput: MutableList<Int> = mutableListOf(0,0,0)
    var tempCheckDate :LocalDate = LocalDate.of(2022, 3,15)

    while (!checkpoint) {
        println("Input the date (yyyy-mm-dd):")

        try {
            tempDateInput = readln().split("-").map { it.toInt() }.toMutableList()
            tempCheckDate = LocalDate.of(tempDateInput[0], tempDateInput[1], tempDateInput[2])
            checkpoint = true
        }
        catch (e: Exception) {
            println("The input date is invalid")
        }
    }
    taskList[tempTaskID].taskDate = kotlinx.datetime.LocalDate(tempDateInput[0], tempDateInput[1], tempDateInput[2] )
    println("The task is changed")
    return checkpoint
}

fun setTaskPriority(tempTaskID: Int): Boolean {
    var checkpoint = false

    println("Input the task priority (C, H, N, L):")
    while (!checkpoint) {
        val input = readln().uppercase()

        when (input) {
            "C" -> {
                taskList[tempTaskID].taskPriority = "C"
                taskList[tempTaskID].taskPriorityC = "\u001B[101m \u001B[0m"
                println("The task is changed")
                checkpoint = true
            }
            "H" -> {
                taskList[tempTaskID].taskPriority = "H"
                taskList[tempTaskID].taskPriorityC = "\u001B[103m \u001B[0m"
                println("The task is changed")
                checkpoint = true
            }
            "N" -> {
                taskList[tempTaskID].taskPriority = "N"
                taskList[tempTaskID].taskPriorityC = "\u001B[102m \u001B[0m"
                println("The task is changed")
                checkpoint = true
            }
            "L" -> {
                taskList[tempTaskID].taskPriority = "L"
                taskList[tempTaskID].taskPriorityC = "\u001B[104m \u001B[0m"
                println("The task is changed")
                checkpoint = true
            }
            else -> println("Input the task priority (C, H, N, L):")
        }
    }
    return checkpoint
}

fun printTasks() {
    val drawTable1 = "+----+------------+-------+---+---+--------------------------------------------+"
    val drawTable2 = "| N  |    Date    | Time  | P | D |                   Task                     |"


    if (taskList.isEmpty()) {

        if (jsonFile.exists()) {
            val existingFile = jsonFile.readText()
            val tempJsonReadList =  Json.decodeFromString<List<taskObject>>(existingFile)

            //println("*** print file content finished")
            for (task in tempJsonReadList.indices) {
                taskList.add(tempJsonReadList[task])
            }
            //println("*** assignment of lists finished")
            //println("*** task returned to app ")

            println(drawTable1)
            println(drawTable2)
            println(drawTable1)

            for (task in taskList.indices) {

                taskList[task].dueDateCalculate()
                for (subTask in taskList[task].currentTasks.indices) {
                    val tempSubTask = taskList[task].currentTasks[subTask].chunked(44)
                    //println(tempInput.taskDateTime.toString().substringAfter("T"))
                    when {
                        subTask == 0 && tempSubTask[0].length < 44 -> {
                            println("| ${task + 1}  | ${taskList[task].taskDate} | ${taskList[task].taskDateTime.toString().substringAfter("T")} | ${taskList[task].taskPriorityC} | ${taskList[task].dueTagC} |${tempSubTask[0].padEnd(44,' ')}|")
                        }
                        subTask == 0 && tempSubTask[0].length >= 44 -> {
                            println("| ${task + 1}  | ${taskList[task].taskDate} | ${taskList[task].taskDateTime.hour}:${taskList[task].taskDateTime.minute} | ${taskList[task].taskPriorityC} | ${taskList[task].dueTagC} |${tempSubTask[0].padEnd(44,' ')}|")
                            for (a in 1 until tempSubTask.size) {
                                println("|    |            |       |   |   |${tempSubTask[a].padEnd(44,' ')}|")
                            }
                        }
                        else -> {
                            for (b in tempSubTask.indices) {
                                println("|    |            |       |   |   |${tempSubTask[b].padEnd(44,' ')}|")
                            }
                        }
                    }
                }
                println(drawTable1)
            }
        } else {
            println("No tasks have been input")
        }
    } else {
        println(drawTable1)
        println(drawTable2)
        println(drawTable1)

        for (task in taskList.indices) {

            taskList[task].dueDateCalculate()

            for (subTask in taskList[task].currentTasks.indices) {
                val tempSubTask = taskList[task].currentTasks[subTask].chunked(44)

                when {
                    subTask == 0 && tempSubTask[0].length < 44 -> {
                        println("| ${task + 1}  | ${taskList[task].taskDate} | ${taskList[task].taskDateTime.toString().substringAfter("T")} | ${taskList[task].taskPriorityC} | ${taskList[task].dueTagC} |${tempSubTask[0].padEnd(44,' ')}|")
                    }
                    subTask == 0 && tempSubTask[0].length >= 44 -> {
                        println("| ${task + 1}  | ${taskList[task].taskDate} | ${taskList[task].taskDateTime.hour}:${taskList[task].taskDateTime.minute} | ${taskList[task].taskPriorityC} | ${taskList[task].dueTagC} |${tempSubTask[0].padEnd(44,' ')}|")
                        for (a in 1 until tempSubTask.size) {
                            println("|    |            |       |   |   |${tempSubTask[a].padEnd(44,' ')}|")
                        }
                    }
                    else -> {
                        for (b in tempSubTask.indices) {
                            println("|    |            |       |   |   |${tempSubTask[b].padEnd(44,' ')}|")
                        }
                    }
                }
            }
            println(drawTable1)
        }
    }
}

fun addTasks() {
    val tempInput = taskObject()
    var input: String
    var checkpoint = false

    println("Input the task priority (C, H, N, L):")
    while (!checkpoint) {
        input = readLine().toString().uppercase()

        when (input) {
            "C" -> {
                tempInput.taskPriority = "C"
                tempInput.taskPriorityC = "\u001B[101m \u001B[0m"
                checkpoint = true
            }
            "H" -> {
                tempInput.taskPriority = "H"
                tempInput.taskPriorityC = "\u001B[103m \u001B[0m"
                checkpoint = true
            }
            "N" -> {
                tempInput.taskPriority = "N"
                tempInput.taskPriorityC = "\u001B[102m \u001B[0m"
                checkpoint = true
            }
            "L" -> {
                tempInput.taskPriority = "L"
                tempInput.taskPriorityC = "\u001B[104m \u001B[0m"
                checkpoint = true
            }
            else -> println("Input the task priority (C, H, N, L):")
        }

    }

    checkpoint = false

    var tempDateInput: MutableList<Int> = mutableListOf(0,0,0)
    var tempCheckDate :LocalDate = LocalDate.of(2022, 3,15)
    while (!checkpoint) {
        println("Input the date (yyyy-mm-dd):")

        try {
            tempDateInput = readln().split("-").map { it.toInt() }.toMutableList()
            tempCheckDate = LocalDate.of(tempDateInput[0], tempDateInput[1], tempDateInput[2])
            checkpoint = true
        }
        catch (e: Exception) {
            println("The input date is invalid")
        }
    }

    checkpoint = false

    var tempTimeInput: MutableList<Int> = mutableListOf(0,0)
    var tempCheckTime: LocalTime = LocalTime.of(22,22)
    while (!checkpoint) {
        println("Input the time (hh:mm):")

        try {
            tempTimeInput = readln().split(":").map { it.toInt() }.toMutableList()
            tempCheckTime = LocalTime.of(tempTimeInput[0], tempTimeInput[1])
            checkpoint = true
        }
        catch (e: Exception) {
            println("The input time is invalid")
        }
    }

    println("Input a new task (enter a blank line to end):")

    while (true) {
        input = readLine().toString().trimIndent()
        if (input.isNullOrEmpty()) break else tempInput.currentTasks.add(input.trimIndent())
    }
    if (tempInput.currentTasks.isEmpty()) {
        println("The task is blank")
    } else {
        tempInput.taskDate = kotlinx.datetime.LocalDate(tempDateInput[0], tempDateInput[1], tempDateInput[2] )
        tempInput.taskTempDate = LocalDate(tempInput.taskDate.year,tempInput.taskDate.monthNumber,tempInput.taskDate.dayOfMonth)
        tempInput.taskTime[0] = tempTimeInput[0]
        tempInput.taskTime[1] = tempTimeInput[1]
        tempInput.taskDateTime = LocalDateTime(tempInput.taskDate.year,tempInput.taskDate.monthNumber,tempInput.taskDate.dayOfMonth, tempInput.taskTime[0], tempInput.taskTime[1])
        tempInput.dueDateCalculate()
        taskList.add(tempInput)
    }
}
