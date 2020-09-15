<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script>
export default {
  name: 'App',
}
</script>

<style>
  body{
    background-color: #f7f7f7;
    margin: 0;
  }
  .el-tabs__header{
    margin: 0;
  }
  .el-textarea .el-input__count {
    bottom: 10px;
    left: 10px;
    right: auto;
  }
</style>
