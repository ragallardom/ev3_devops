<template>
  <div class="app-container">
    <!-- Header Section -->
    <header class="app-header">
      <div class="brand-section">
        <div class="brand-logo">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" width="32" height="32">
            <path d="M12 22c5.523 0 10-4.477 10-10S17.523 2 12 2 2 6.477 2 12s4.477 10 10 10z"/>
            <path d="m9 12 2 2 4-4"/>
          </svg>
        </div>
        <h1 class="brand-title">Taskflow</h1>
      </div>
      
      <button class="btn-primary" @click="openCreateModal">
        <Plus :size="18" />
        Nueva Tarea
      </button>
    </header>

    <!-- Error Banner -->
    <div v-if="error" class="error-banner">
      <AlertTriangle :size="20" />
      <span>{{ error }}</span>
      <button class="action-btn" style="margin-left: auto; color: inherit;" @click="error = null">✕</button>
    </div>

    <!-- Main Dashboard Section -->
    <div v-if="loading" class="spinner-container">
      <div class="spinner"></div>
      <p style="color: var(--text-secondary); font-size: 0.9rem;">Cargando tareas...</p>
    </div>

    <div v-else>
      <!-- Dashboard Stats Widgets -->
      <DashboardStats :tasks="tasks" />

      <!-- Kanban Task Board -->
      <TaskBoard 
        :tasks="tasks" 
        @edit-task="openEditModal"
        @delete-task="deleteTask"
        @update-status="updateTaskStatus"
      />
    </div>

    <!-- Create / Edit Modal -->
    <TaskModal 
      :is-open="showModal"
      :task-to-edit="editingTask"
      @close="closeModal"
      @submit="submitTask"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { Plus, AlertTriangle } from 'lucide-vue-next';
import DashboardStats from './components/DashboardStats.vue';
import TaskBoard from './components/TaskBoard.vue';
import TaskModal from './components/TaskModal.vue';

const tasks = ref([]);
const loading = ref(true);
const error = ref(null);

const showModal = ref(false);
const editingTask = ref(null);

// Fetch all tasks from backend
const fetchTasks = async () => {
  loading.value = true;
  error.value = null;
  try {
    const res = await fetch('/api/tasks');
    if (!res.ok) {
      throw new Error(`Error en el servidor: ${res.statusText}`);
    }
    const data = await res.json();
    tasks.value = data;
  } catch (err) {
    console.error('Error fetching tasks:', err);
    error.value = 'No se pudieron cargar las tareas. Asegúrate de que el backend esté disponible.';
  } finally {
    loading.value = false;
  }
};

// Create or update task handler
const submitTask = async (taskData) => {
  error.value = null;
  const isEdit = !!taskData.id;
  const url = isEdit ? `/api/tasks/${taskData.id}` : '/api/tasks';
  const method = isEdit ? 'PUT' : 'POST';

  try {
    const res = await fetch(url, {
      method,
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(taskData)
    });

    if (!res.ok) {
      const errBody = await res.json().catch(() => ({}));
      throw new Error(errBody.message || `Error al guardar la tarea. Código ${res.status}`);
    }

    closeModal();
    await fetchTasks();
  } catch (err) {
    console.error('Error submitting task:', err);
    error.value = `No se pudo guardar la tarea: ${err.message}`;
  }
};

// Patch status handler (quick shift)
const updateTaskStatus = async (id, newStatus) => {
  error.value = null;
  
  // Optimistic update for fluid UI response
  const originalTasks = [...tasks.value];
  const targetTask = tasks.value.find(t => t.id === id);
  if (targetTask) {
    targetTask.status = newStatus;
  }

  try {
    const res = await fetch(`/api/tasks/${id}/status`, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ status: newStatus })
    });

    if (!res.ok) {
      throw new Error(`Error al actualizar estado. Código ${res.status}`);
    }
    
    // Refresh to get actual updated timestamps
    await fetchTasks();
  } catch (err) {
    console.error('Error updating status:', err);
    error.value = 'No se pudo actualizar el estado de la tarea.';
    // Revert optimistic update
    tasks.value = originalTasks;
  }
};

// Delete task handler
const deleteTask = async (id) => {
  if (!confirm('¿Estás seguro de que deseas eliminar esta tarea?')) return;
  
  error.value = null;
  try {
    const res = await fetch(`/api/tasks/${id}`, {
      method: 'DELETE'
    });

    if (!res.ok) {
      throw new Error(`Error al eliminar la tarea. Código ${res.status}`);
    }

    await fetchTasks();
  } catch (err) {
    console.error('Error deleting task:', err);
    error.value = 'No se pudo eliminar la tarea.';
  }
};

// Modal triggers
const openCreateModal = () => {
  editingTask.value = null;
  showModal.value = true;
};

const openEditModal = (task) => {
  editingTask.value = task;
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
  editingTask.value = null;
};

onMounted(() => {
  fetchTasks();
});
</script>
