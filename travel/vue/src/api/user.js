import request from '@/utils/request'

export function login(data) {
    return request({
        url: '/api/auth/login',
        method: 'post',
        data
    })
}


export function register(data) {
    // 使用request方法发送HTTP请求
    return request({
        url: '/api/auth/register', // 请求的URL地址
        method: 'post', // 请求方法为POST
        data // 要发送的请求数据
    })
}