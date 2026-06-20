<template>
  <div v-if="isOpen" class="modal-overlay" @click.self="$emit('close')">
    <div class="glass-panel modal-content">
      <div class="modal-header">
        <h2 class="modal-title">{{ isEditMode ? 'Editar Tarea' : 'Nueva Tarea' }}</h2>
        <button class="action-btn" title="Cerrar" @click="$emit('close')">
          <X :size="20" />
        </button>
      </div>

      <form @submit.prevent="handleSubmit" class="modal-form">
        <div style="display: flex; flex-direction: column; gap: 1.25rem;">
          <!-- Title -->
          <div class="form-group">
            <label class="form-label" for="title">Título *</label>
            <input 
              v-model="form.title" 
              type="text" 
              id="title" 
              required 
              placeholder="Ej: Desplegar a producción" 
              class="form-input"
              ref="titleInput"
            />
          </div>

          <!-- Description -->
          <div class="form-group">
            <label class="form-label" for="description">Descripción</label>
            <textarea 
              v-model="form.description" 
              id="description" 
              placeholder="Describe detalladamente el objetivo de la tarea..." 
              class="form-textarea"
            ></textarea>
          </div>

          <!-- Selection row: Status & Priority -->
          <div class="form-row">
            <div class="form-group">
              <label class="form-label" for="status">Estado</label>
              <select v-model="form.status" id="status" class="form-select">
                <option value="TODO">Pendiente</option>
                <option value="IN_PROGRESS">En Progreso</option>
                <option value="DONE">Completada</option>
              </select>
            </div>
            
            <div class="form-group">
              <label class="form-label" for="priority">Prioridad</label>
              <select v-model="form.priority" id="priority" class="form-select">
                <option value="LOW">Baja</option>
                <option value="MEDIUM">Media</option>
                <option value="HIGH">Alta</option>
              </select>
            </div>
          </div>

          <!-- Due Date -->
          <div class="form-group">
            <label class="form-label" for="dueDate">Fecha Límite</label>
            <input 
              v-model="form.dueDate" 
              type="date" 
              id="dueDate" 
              class="form-input" 
            />
          </div>

          <!-- Error Feedback -->
          <div v-if="validationError" style="color: #f87171; font-size: 0.85rem; font-weight: 500;">
            {{ validationError }}
          </div>

          <!-- Footer Actions -->
          <div class="modal-footer">
            <button type="button" class="btn-secondary" @click="$emit('close')">
              Cancelar
            </button>
            <button type="submit" class="btn-primary">
              {{ isEditMode ? 'Guardar Cambios' : 'Crear Tarea' }}
            </button>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue';
import { X } from 'lucide-vue-next';

const props = defineProps({
  isOpen: {
    type: Boolean,
    required: true
  },
  taskToEdit: {
    type: Object,
    default: null
  }
});

const emit = defineEmits(['close', 'submit']);

const isEditMode = computed(() => !!props.taskToEdit);
const validationError = ref('');
const titleInput = ref(null);

const form = reactive({
  id: null,
  title: '',
  description: '',
  status: 'TODO',
  priority: 'MEDIUM',
  dueDate: ''
});

// Helper to convert LocalDateTime string (e.g. 2026-06-20T15:10:00) to Date format YYYY-MM-DD
const formatDateForInput = (dateTimeStr) => {
  if (!dateTimeStr) return '';
  return dateTimeStr.split('T')[0];
};

// Reset form to blank / default settings
const resetForm = () => {
  form.id = null;
  form.title = '';
  form.description = '';
  form.status = 'TODO';
  form.priority = 'MEDIUM';
  form.dueDate = '';
  validationError.value = '';
};

// Initialize form depending on edit mode or create mode
const initForm = () => {
  if (props.taskToEdit) {
    form.id = props.taskToEdit.id;
    form.title = props.taskToEdit.title;
    form.description = props.taskToEdit.description || '';
    form.status = props.taskToEdit.status;
    form.priority = props.taskToEdit.priority;
    form.dueDate = formatDateForInput(props.taskToEdit.dueDate);
  } else {
    resetForm();
  }
};

watch(() => props.isOpen, (isOpenVal) => {
  if (isOpenVal) {
    initForm();
    // Focus title input on modal load
    setTimeout(() => {
      if (titleInput.value) {
        titleInput.value.focus();
      }
    }, 50);
  }
});

watch(() => props.taskToEdit, () => {
  if (props.isOpen) {
    initForm();
  }
}, { deep: true });

const handleSubmit = () => {
  if (!form.title.trim()) {
    validationError.value = 'El título de la tarea es requerido';
    return;
  }
  
  // Format the date to ISO 8601 LocalDateTime format (adding a time component e.g. T23:59:59 or T12:00:00)
  // which Spring Boot's LocalDateTime mapping expects.
  let formattedIsoDate = null;
  if (form.dueDate) {
    formattedIsoDate = `${form.dueDate}T12:00:00`;
  }

  emit('submit', {
    id: form.id,
    title: form.title.trim(),
    description: form.description.trim(),
    status: form.status,
    priority: form.priority,
    dueDate: formattedIsoDate
  });
};
</script>
