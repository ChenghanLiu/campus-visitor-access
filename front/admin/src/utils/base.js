const base = {
    get() {
        return {
            url : "",
            name: "",
            // 退出到首页链接
            indexUrl: 'http://localhost:8080/front/dist/index.html'
        };
    },
    getProjectName(){
        return {
            projectName: "高校宿舍访客预约管理系统"
        } 
    }
}
export default base
