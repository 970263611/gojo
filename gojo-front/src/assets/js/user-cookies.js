import {activeChat, group2me, groupChat, user2me, userChat} from "./const";
//加载用户好友和群组信息
export const getUserCookies = function (cookies, http) {
  // if (!cookies.isKey(user2me)) {
    http.post('/gojo/getUserFriends').then(function (res) {
      if (res.data.success) {
        cookies.set(user2me, JSON.stringify(res.data.obj), "1y")
      }
    }).catch(function (error) {
      console.log(error)
    })
  // }
  // if (!cookies.isKey(group2me)) {
    http.post('/gojo/getUserGroups').then(function (res) {
      if (res.data.success) {
        cookies.set(group2me, JSON.stringify(res.data.obj), "1y")
      }
    }).catch(function (error) {
      console.log(error);
    });
  // }
}

//添加用户好友或者陌生人信息
// export const addUserCookies = function (cookies,type) {
//   let key
//   const userMsgs = {}
//   if('user' === type){
//     key = user2me
//   }else if('group' === type){
//     key = group2me
//   }
//   if(isCorrect(cookies,key)){
//     userMsgs[key] = cookies.get(key)
//   }else{
//     debugger
//   }
// }

//展示用户好友和群组信息
export const setUserCookies = function (cookies) {
  const userMsgs = {}
  if(isCorrect(cookies,user2me)){
    userMsgs.friends = cookies.get(user2me)
  }
  if(isCorrect(cookies,group2me)){
    userMsgs.groups = cookies.get(group2me)
  }
  return userMsgs
}

//获取聊天记录
export const getUserChatMsg = function (cookies,type,id) {
  let key = ''
  if('user' === type){
    key = userChat
  }else if('group' === type){
    key = groupChat
  }
  if(isCorrect(cookies,key)){
    const map = cookies.get(key)
    return map[id]
  }
  return ''
}

//id,msg,flag,username,time  存储聊天记录
export const setUserChatMsg = async function (cookies,type,obj) {
  let map = {}
  let chatAry = new Array()
  let key = ''
  if('user' === type){
    key = userChat
  }else if('group' === type){
    key = groupChat
  }
  if(isCorrect(cookies,key)) {
    map = cookies.get(key)
  }
  if(map && map[obj.id] != undefined){
    chatAry = map[obj.id]
  }
  chatAry.push(obj)
  map[obj.id] = chatAry
  cookies.set(key,map,"1y")
}

export const setActiveChats = function (cookies,obj){
  let result = {}
  let set = new Set()
  if(isCorrect(cookies,activeChat)){
    result = cookies.get(activeChat)
    for(let o of result.set){
      if(o.id === obj.id){
        result.set.splice(result.set.indexOf(o), 1)
      }
    }
    result.set.push(obj)
  }else{
    set.add(obj)
    result.set = set
  }
  cookies.set(activeChat,result,"1y")
}

export const getActiveChats = function (cookies){
  if(isCorrect(cookies,activeChat)){
    return cookies.get(activeChat).set.reverse()
  }
}

function isCorrect(cookies,name){
  if(cookies.isKey(name) && cookies.get(name) != '' && cookies.get(name) != null){
    return true
  }
  return false
}


