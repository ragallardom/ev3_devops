<template>
  <div class="board-grid">
    <!-- Columna: Pendientes -->
    <div class="glass-panel board-column">
      <div class="column-header">
        <div class="column-title-wrapper">
          <div class="column-dot todo"></div>
          <h2 class="column-title">Por Hacer</h2>
        </div>
        <span class="column-count">{{ todoTasks.length }}</span>
      </div>
      
      <div class="column-cards-container">
        <div v-if="todoTasks.length === 0" class="empty-state">
          <Inbox :size="32" />
          <p>Sin tareas pendientes</p>
        </div>
        <TaskCard 
          v-for="task in todoTasks" 
          :key="task.id" 
          :task="task"
          @edit="$emit('edit-task', $event)"
          @delete="$emit('delete-task', $event)"
          @update-status="$emit('update-status', $event, 'IN_PROGRESS')"
        />
      </div>
    </div>

    <!-- Columna: En Progreso -->
    <div class="glass-panel board-column">
      <div class="column-header">
        <div class="column-title-wrapper">
          <div class="column-dot progress"></div>
          <h2 class="column-title">En Progreso</h2>
        </div>
        <span class="column-count">{{ progressTasks.length }}</span>
      </div>
      
      <div class="column-cards-container">
        <div v-if="progressTasks.length === 0" class="empty-state">
          <Activity :size="32" />
          <p>Ninguna tarea activa</p>
        </div>
        <TaskCard 
          v-for="task in progressTasks" 
          :key="task.id" 
          :task="task"
          @edit="$emit('edit-task', $event)"
          @delete="$emit('delete-task', $event)"
          @update-status="$emit('update-status', $event, $event)"
        />
      </div>
    </div>

    <!-- Columna: Completadas -->
    <div class="glass-panel board-column">
      <div class="column-header">
        <div class="column-title-wrapper">
          <div class="column-dot done"></div>
          <h2 class="column-title">Completadas</h2>
        </div>
        <span class="column-count">{{ doneTasks.length }}</span>
      </div>
      
      <div class="column-cards-container">
        <div v-if="doneTasks.length === 0" class="empty-state">
          <CheckSquare :size="32" />
          <p>No hay tareas terminadas</p>
        </div>
        <TaskCard 
          v-for="task in doneTasks" 
          :key="task.id" 
          :task="task"
          @edit="$emit('edit-task', $event)"
          @delete="$emit('delete-task', $event)"
          @update-status="$emit('update-status', $event, 'IN_PROGRESS')"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { Inbox, Activity, CheckSquare } from 'lucide-vue-next';
import TaskCard from './TaskCard.vue';

const props = defineProps({
  tasks: {
    type: Array,
    required: true
  }
});

defineEmits(['edit-task', 'delete-task', 'update-status']);

const todoTasks = computed(() => 
  props.tasks.filter(t => t.status === 'TODO')
);

const progressTasks = computed(() => 
  props.tasks.filter(t => t.status === 'IN_PROGRESS')
);

const doneTasks = computed(() => 
  props.tasks.filter(t => t.status === 'DONE')
);
</script>
