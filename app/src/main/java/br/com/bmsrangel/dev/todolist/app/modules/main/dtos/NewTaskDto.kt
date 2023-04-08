package br.com.bmsrangel.dev.todolist.app.modules.main.dtos

data class NewTaskDto(val description: String, val dueDate: String) {
    fun toMap(): HashMap<String, Any> {
        return hashMapOf(
            Pair("description", description),
            Pair("due_date", dueDate)
        )
    }
}
