# uberPaidValidator
Uber paid validator


https://uberpaidvalidator.herokuapp.com/
 
 
Deploy: `sbt herokuDeploy`

Tools:


https://enear.github.io/2016/03/31/parser-combinators/

https://github.com/enear/parser-combinators-tutorial

https://github.com/henrikerola/scaladin

https://github.com/earldouglas/xsbt-web-plugin

https://github.com/earldouglas/sbt-heroku-deploy

https://github.com/earldouglas/sbt-heroku-deploy/issues/10#issuecomment-283180866
// resolvers += Resolver.url("earldouglas", new URL("https://dl.bintray.com/earldouglas/sbt-plugins/"))(Resolver.ivyStylePatterns)  // heroku deploy


Production Mode:

Vaadin 7 comes with built in support for Sass, which can be thought of as a preprocessor for CSS. From the Sass homepage:
When you have your application in development mode the SCSS theme will get compiled into CSS on-the-fly. 
When you switch to production mode you will need to compile the scss theme into CSS manually using the SASS compiler and add the produced CSS to the project.

The production mode can be enabled, and debug mode thereby disabled, by adding a productionMode=true parameter to the servlet context in the web.xml deployment descriptor: 
    src/main/webapp/WEB-INF/web.xml
    
    
04:25:23 {master} ~/Development/uberPaidValidator/src/main/webapp/VAADIN/themes/dashboard$ java -classpath "/home/user/Development/uberPaidValidator/target/webapp/WEB-INF/lib/*" com.vaadin.sass.SassCompiler styles.scss styles.css
Mar 04, 2017 4:27:20 PM com.vaadin.sass.internal.handler.SCSSErrorHandler severe
SEVERE: Mixin Definition: keyframes not found
Mar 04, 2017 4:27:21 PM com.vaadin.sass.internal.handler.SCSSErrorHandler severe
SEVERE: Mixin Definition: base-common not found
