module.exports = function (grunt) {
    // 项目配置
    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        concat: {
            options: {
                separator: '\n\r'
            },
            dist: {
                src: [
                    'src/Global.js',//无依赖
                    'src/mvc/Notification.js',//无依赖
                    'src/mvc/ViewManager.js',//无依赖
					'src/http/HttpConfigNormal.js',//无依赖
                    'src/http/HttpConfig.js',//无依赖
                    'src/http/HttpResultFilter.js',//无依赖
					'src/http/SendParamNormal.js',//$T.httpConfig
                    'src/http/SendParam.js',//$T.httpConfig
                    'src/http/TestFilter.js',//无依赖
                    'src/tools/ArrayTools.js',//无依赖
                    'src/display/DisplayObject.js',//无依赖
                    'src/event/Event.js',//无依赖
                    'src/event/EventPool.js',//Event
                    'src/event/EventDispatcher.js',//$T.arrayTools、Event、$T.eventPool
                    'src/tween/TweenEventType.js',//无依赖
                    'src/tween/DelayedCall.js',//$T.tweenEventType、EventDispatcher
                    'src/tween/DelayedCallPool.js',//DelayedCall
                    'src/tween/Transitions.js',//$T.transitions
                    'src/tween/Tween.js',//$T.transitions、$T.tweenEventType、EventDispatcher
                    'src/tween/TweenPool.js',//Tween
                    'src/tween/Juggler.js',//$T.arrayTools、$T.tweenEventType、$T.delayedCallPool、$T.tweenPool、DelayedCall、Tween、EventDispatcher
                    'src/tween/JugglerManager.js',//$T.jugglerManager、Juggler
                    'src/Version.js',//$T.jugglerManager
					'src/http/HttpUtilNormal.js',//$T.httpConfig、$T.httpUtil、$T.viewManager、$T.notification、$T.version、$T.httpResultFilter
                    'src/http/HttpUtil.js',//$T.httpConfig、$T.httpUtil、$T.viewManager、$T.notification、$T.version、$T.httpResultFilter
                    'src/resource/ResourceEventType.js',//无依赖
                    'src/resource/Loader.js',//$T.httpConfig、$T.httpUtil、$T.resourceEventType、EventDispatcher
                    'src/resource/ResourceManager.js',//$T.resourceEventType、Loader
                    'src/Register.js',//$T.viewManager、$T.jugglerManager
                    'src/module/ModuleData.js',
                    'src/module/ModuleManager.js',//$T.resourceManager、ModuleData、$T.register、$T.viewManager、$T.notification
                    'src/websocket/WebSocketConfig.js',//无依赖
                    'src/websocket/WebSocketEventType.js',//无依赖
                    'src/websocket/WebSocketClient.js'//$T.webSocketEventType、$T.webSocketConfig、EventDispatcher
                ],
                dest: '../dist/<%= pkg.name %>.js'
            }
        },
        uglify: {
            build: {
                src: '../dist/<%= pkg.name %>.js',
                dest: '../dist/<%= pkg.name %>.min.js'
            }
        }
    });
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-concat');
    // 默认任务
    grunt.registerTask('default', ['concat', 'uglify']);
}