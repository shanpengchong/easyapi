###  **_# easyapi

#### 介绍_** 

一款java接口文档自动生成的插件，包含了根据接口注释或注解自动生成接口文档，可网页打开。
页功能部分（所有功能都可以自由编辑保存）：
1. 首页-文档介绍
1. 首页-请求环境配置（可配置多个地址，用于模拟请求）
1. 首页-返回参数外包装配置（可手动配置项目对返回数据的包装结构）
1. 首页-同步文档（将文档向指定地址发送同步，目标地址成功接收后会更新自己的文档）
1. 这里是列表文本
1. 这里是列表文本
1. 菜单栏-搜索（可根据接口名称和接口请求路径模糊搜索接口，支持拼音搜索）
1. 菜单栏-控制器列表（controller）
1. 菜单栏-接口列表
1. 菜单栏-删除接口
1. 菜单栏-导入接口（将其他接口文档导入导本地）
1. 接口详情-修改接口基本信息
1. 接口详情-导出接口结构
1. 这里是列表文本
1. 接口详情-选择环境
1. 接口详情-接口返回mock（自动生成假数据）
1. 接口详情-接口真实请求
1. 接口详情-选择参数填写方式（json或form）
1. 接口详情-修改接口入参（字段隐藏，字段必填，字段类型。。。）
1. 接口详情-修改接口返回值
1. 接口详情-导入导出接口入参和返回



#### 软件架构
1.使用springboot-2.1.3核心架构
2.页面呈现使用VUE
3.数据存取采用本地json文件




#### 安装教程

1.  clone项目
2.  maven install
3.  项目引入maven

    `<dependency>
       <groupId>cn.easyutil</groupId>
       <artifactId>easyapi/artifactId>
       <version>0.1</version>
    </dependency>`        
    
    

#### 使用说明

1.  项目配置和启动
    项目只需要在yml或propertite文件中配置easyapi.enable=true即可使用    

2.  默认配置，默认文档内容文件生成到项目根目录，使用项目名-api作为文件名，
默认每次重启不删除旧的构建，默认每次重启都会补充扫描

3.  扩展-配置基本信息（通过配置文件的话可以不用写配置类）
    
    `
    @Configuration
    public class MyApiConfiguration extends DefaultConfigurationCreator{

        @Override
        public EasyapiConfiguration replenish(EasyapiConfiguration configuration) {

            //设置接口文档文件路径
            configuration.setApiFilePath("D:/");
            //设置接口文档文件夹名称
            configuration.setProjectName("easyapi");
            //启动时是否重新扫描，有新增的接口会添加
            configuration.setRescan(true);
            //启动时是否删除旧的构建
            configuration.setDelOld(false);
            //扫描的包路径，不设置默认扫描整个项目
            configuration.setPackageName("cn.easyutil");
            //启动时是否自动同步到目标机器
            configuration.setSync(true);
            //目标机器项目地址
            configuration.setSyncAddress("127.0.0.1");
            //目标机器项目端口
            configuration.setSyncPort(8080);
            return configuration;
        }
    }
    `

4.扩展-高级-拦截器配置
    `

        @Configuration
        public class MyApiConfiguration extends DefaultConfigurationCreator{
            @Override
            public EasyapiConfiguration start(EasyapiConfiguration configuration) {
                //读取参数实体相关信息
                configuration.setReadBeanApiFilter(new DefaultReadBeanApi());
                //读取controller相关信息
                configuration.setReadControllerApiFilter(new DefaultReadControllerApi());
                //读取接口方法相关信息
                configuration.setReadInterfaceApiFilter(new DefaultReadInterfaceApi());
                //读取接口请求参数相关信息
                configuration.setReadRequestParamApiFilter(new DefaultReadRequestParamApi());
                //读取接口返回值相关信息
                configuration.setReadResponseParamApiFilter(new DefaultReadResponseParamApi());
                return configuration;
            }
        }
    `
    拦截器都配有默认拦截器，可以通过实现相关filter自主配置拦截器，也可以通过继承默认拦截器补充个性化配置