import { createApp } from 'vue'
import { createPinia } from 'pinia'
import vant from 'vant'
import 'vant/lib/index.css'

import App from './App.vue'
import router from './router'
import './assets/styles/main.scss'
import permission from './directives/permission'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(vant)
app.use(permission)

app.mount('#app')
