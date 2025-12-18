import request from "./request";

export function getlist() {
    return request({
        url:"/goods.json",
        method: 'get',
    })
}

export function loginUser(data) {
  return request({
    url:"/login",
    method: 'post',
    data 
  });
}