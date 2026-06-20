<template>
  <div class="stats-grid">
    <!-- Total Tasks -->
    <div class="glass-panel stat-card">
      <div class="stat-info">
        <span class="stat-label">Total Tareas</span>
        <span class="stat-value">{{ totalCount }}</span>
      </div>
      <div class="stat-icon-wrapper">
        <ListTodo :size="24" class="icon-todo" style="color: #818cf8;" />
      </div>
    </div>

    <!-- Pending Tasks -->
    <div class="glass-panel stat-card">
      <div class="stat-info">
        <span class="stat-label">Pendientes</span>
        <span class="stat-value">{{ todoCount }}</span>
      </div>
      <div class="stat-icon-wrapper">
        <Clock :size="24" class="icon-pending" style="color: #60a5fa;" />
      </div>
    </div>

    <!-- In Progress Tasks -->
    <div class="glass-panel stat-card">
      <div class="stat-info">
        <span class="stat-label">En Progreso</span>
        <span class="stat-value">{{ progressCount }}</span>
      </div>
      <div class="stat-icon-wrapper">
        <Loader2 :size="24" class="icon-progress" style="color: #fbbf24; animation: spin 4s linear infinite;" />
      </div>
    </div>

    <!-- Completed Tasks -->
    <div class="glass-panel stat-card">
      <div class="stat-info">
        <span class="stat-label">Completadas</span>
        <span class="stat-value">{{ doneCount }}</span>
      </div>
      <div class="stat-icon-wrapper">
        <CheckCircle2 :size="24" class="icon-done" style="color: #34d399;" />
      </div>
    </div>

    <!-- Completion Rate -->
    <div class="glass-panel stat-card">
      <div class="stat-info">
        <span class="stat-label">Tasa de Éxito</span>
        <span class="stat-value">{{ completionRate }}%</span>
      </div>
      <div class="stat-icon-wrapper">
        <TrendingUp :size="24" class="icon-rate" style="color: #a78bfa;" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { ListTodo, Clock, Loader2, CheckCircle2, TrendingUp } from 'lucide-vue-next';

const props = defineProps({
  tasks: {
    type: Array,
    required: true
  }
});

const totalCount = computed(() => props.tasks.length);

const todoCount = computed(() => 
  props.tasks.filter(t => t.status === 'TODO').length
);

const progressCount = computed(() => 
  props.tasks.filter(t => t.status === 'IN_PROGRESS').length
);

const doneCount = computed(() => 
  props.tasks.filter(t => t.status === 'DONE').length
);

const completionRate = computed(() => {
  if (totalCount.value === 0) return 0;
  return Math.round((doneCount.value / totalCount.value) * 100);
});
</script>
