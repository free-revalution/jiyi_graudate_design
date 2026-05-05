<template>
  <div class="table-box page-container">

  </div>
</template>

<script setup lang="tsx" name="communityPostPageSetting">


</script>


<style scoped lang="scss">
$select-width: 400px;
$input-width: 400px;






.notification-dialog-content {
  margin-top: 20px;
}

.notification-title-input,
.notification-content-input,
.notification-time-input {
  width: $input-width;
}

:deep(.notification-title-input .el-input__wrapper),
:deep(.notification-content-input .el-input__wrapper),
:deep(.notification-time-input .el-input__wrapper) {
  border: none !important;
  border-bottom: 1px solid #e0e0e0 !important;
  border-radius: 0 !important;
  box-shadow: none !important;
}

.cu-select-0030 {
  width: $select-width;
}

:deep(.cu-select-0030 .el-select__wrapper) {
  border: none !important;
  border-bottom: 1px solid #e0e0e0 !important;
  border-radius: 0 !important;
  box-shadow: none !important;
}

.page-container {
  padding: 20px;
  background-color: var(--el-bg-color);
  height: 100%;
}
</style>