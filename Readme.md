# Spring/SpringMVC/SpringBoot学习
Spring全家桶是目前企业应用开发事实上的标准，在整个IT行业特别是中国，占有很大的比重。但Spring发展至今，已经是一个庞大家族。
如果不看源码单单照着参考文档学习，那么如同隔靴搔痒，出了问题还是不知道原理，不知道如何解决，只能求度娘，使得多数初学者变成了面向百度编程。
如果尝试去学习源码，往往又淹没在海量的代码中。有鉴于此，笔者根据自己多年经验，尝试用尽可能少的代码，演示spring的基本原理和基本概念。
以便初学者可以尽快掌握到精髓。另外，笔者在这里声明下，笔者本人并未通读spring的代码，甚至核心代码也未作深入研究，
仅仅是根据对其原理的个人理解来实现本项目。也可以说是对spring的重新实现。所以，本项目并不是spring的简化版，
实现上也不是与其一一对应。本项目完全是正向工程，最多在遇到困难时查找和参考下spring。

****
## 整体架构
本演示系统大体上分3个部分，核心容器、mvc框架和boot

### 核心ioc容器
演示spring基本功能ioc/di,主要是annotation/bean/context/config
1. annotation包含实现ioc功能用到的注解
2. bean包含bean定义和生成的代码
3. context为对外使用接口
4. config为配置解析生成context的工厂
### mvc框架
如同springmvc基于spring核心之上，本项目的mvc框架也是基于自己的核心容器，可以理解为对核心容器的一个典型应用。其主要有
web.annotation/web.base/web.view
1. web.annotation包含实现mvc功能特有的注解
2. web.base包含实现mvc框架核心的一个DispatchServlet和请求处理器等
3. web.view包含对各类view的实现
### boot(springboot)
boot部分演示springboot的实现，主要包括一个嵌入式的jetty服务器和快速启动的配置
******************
>注意：本项目的框架，Bean暂时不支持scope的概念，所有bean的是单例模式。暂时未对Bean间的依赖做深入分析。
