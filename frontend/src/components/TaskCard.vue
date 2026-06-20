<template>
  <div class="task-card">
    <!-- Colored accent bar representing status -->
    <div :class="['task-card-accent-bar', task.status.toLowerCase()]"></div>
    
    <div class="task-card-header">
      <span :class="['task-priority-tag', task.priority.toLowerCase()]">
        {{ task.priority }}
      </span>
      <div class="task-card-actions">
        <!-- Edit Action -->
        <button class="action-btn" title="Editar tarea" @click="$emit('edit', task)">
          <Edit2 :size="14" />
        </button>
        <!-- Delete Action -->
        <button class="action-btn delete-btn" title="Eliminar tarea" @click="$emit('delete', task.id)">
          <Trash2 :size="14" />
        </button>
      </div>
    </div>

    <!-- Title and Description -->
    <h3 class="task-card-title">{{ task.title }}</h3>
    <p v-if="task.description" class="task-card-desc">{{ task.description }}</p>

    <!-- Footer: due date + status shift options -->
    <div class="task-card-footer">
      <div :class="['task-date-info', { 'overdue': isOverdue }]">
        <Calendar :size="12" />
        <span>{{ formattedDueDate }}</span>
      </div>
      
      <!-- Quick Status Shifts -->
      <div class="task-quick-moves">
        <button 
          v-if="task.status !== 'TODO'" 
          class="action-btn" 
          title="Mover a Pendientes"
          @click="$emit('update-status', task.id, 'TODO')"
        >
          <ArrowLeft :size="13" />
        </button>
        <button 
          v-if="task.status === 'TODO'" 
          class="action-btn" 
          title="Iniciar tarea"
          @click="$emit('update-status', task.id, 'IN_PROGRESS')"
        >
          <Play :size="13" />
        </button>
        <button 
          v-if="task.status === 'IN_PROGRESS'" 
          class="action-btn" 
          title="Completar tarea"
          @click="$emit('update-status', task.id, 'DONE')"
        >
          <Check :size="13" />
        </button>
        <button 
          v-if="task.status === 'DONE'" 
          class="action-btn" 
          title="Reabrir tarea"
          @click="$emit('update-status', task.id, 'IN_PROGRESS')"
        >
          <RotateCcw :size="13" />
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { Edit2, Trash2, Calendar, ArrowLeft, Play, Check, RotateCcw } from 'lucide-vue-next';

const props = defineProps({
  task: {
    type: Object,
    required: true
  }
});

defineEmits(['edit', 'delete', 'update-status']);

// Determine if the task is overdue (past due date & not completed)
const isOverdue = computed(() => {
  if (!props.task.dueDate || props.task.status === 'DONE') return false;
  return new Date(props.task.dueDate) < new Date();
});

// Format the due date in a friendly format
const formattedDueDate = computed(() => {
  if (!props.task.dueDate) return 'Sin fecha límite';
  
  const d = new Date(props.task.dueDate);
  const now = new Date();
  
  // Reset time portions for comparison
  const dDate = new Date(d.getFullYear(), d.getMonth(), d.getDate());
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
  
  const diffTime = dDate - today;
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  
  if (diffDays === 0) return 'Hoy';
  if (diffDays === 1) return 'Mañana';
  if (diffDays === -1) return 'Ayer';
  
  if (diffDays > 1 && diffDays < 7) {
    const weekday = d.toLocaleDateString('es-ES', { weekday: 'long' });
    return weekday.charAt(0).toUpperCase() + weekday.slice(1);
  }
  
  return d.toLocaleDateString('es-ES', { day: 'numeric', month: 'short' });
});
</script>
