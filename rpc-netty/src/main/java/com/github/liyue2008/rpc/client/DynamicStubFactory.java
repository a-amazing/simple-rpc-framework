/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.liyue2008.rpc.client;

import com.github.liyue2008.rpc.transport.Transport;
import com.itranswarp.compiler.JavaStringCompiler;


import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author LiYue
 * Date: 2019/9/27
 */
public class DynamicStubFactory implements StubFactory {
    private final static String STUB_SOURCE_TEMPLATE =
            "package com.github.liyue2008.rpc.client.stubs;\n" +
                    "import com.github.liyue2008.rpc.serialize.SerializeSupport;\n" +
                    "\n" +
                    "public class %s extends AbstractStub implements %s {\n" +
                    "    @Override\n" +
                    "    public String %s(String arg) {\n" +
                    "        return SerializeSupport.parse(\n" +
                    "                invokeRemote(\n" +
                    "                        new RpcRequest(\n" +
                    "                                \"%s\",\n" +
                    "                                \"%s\",\n" +
                    "                                SerializeSupport.serialize(arg)\n" +
                    "                        )\n" +
                    "                )\n" +
                    "        );\n" +
                    "    }\n" +
                    "}";

    @Override
    @SuppressWarnings("unchecked")
    public <T> T createStub(Transport transport, Class<T> serviceClass) {
        try {
            // 填充模板
            String stubSimpleName = serviceClass.getSimpleName() + "Stub";
            String classFullName = serviceClass.getName();
            String stubFullName = "com.github.liyue2008.rpc.client.stubs." + stubSimpleName;
            String methodName = serviceClass.getMethods()[0].getName();

//            StringBuilder sb = new StringBuilder();
//            Method[] methods = serviceClass.getDeclaredMethods();
//            for (Method method : methods) {
//                Class<?> returnType = method.getReturnType();
//                //遍历填充方法的部分内容
//                /**
//                 * @Override\n" +
//                "    public String %s(String arg) {\n" +
//                "        return SerializeSupport.parse(\n" +
//                "                invokeRemote(\n" +
//                "                        new RpcRequest(\n" +
//                "                                \"%s\",\n" +
//                "                                \"%s\",\n" +
//                "                                SerializeSupport.serialize(arg)\n" +
//                "                        )\n" +
//                "                )\n" +
//                "        );\n" +
//                "    }\n" +
//                 */
//                //将多个方法的内容加起来
//                sb.append("method content");
//            }
//
//            //填充类信息
//            String classTemplate = "package com.github.liyue2008.rpc.client.stubs;\n" +
//                    "import com.github.liyue2008.rpc.serialize.SerializeSupport;\n" +
//                    "\n" +
//                    "public class %s extends AbstractStub implements %s {\n" +
//                    "\n";
//            StringBuilder sb2 = new StringBuilder(classTemplate);
//            sb2.append(sb);
//            sb2.append("}");//补全大括号

            String source = String.format(STUB_SOURCE_TEMPLATE, stubSimpleName, classFullName, methodName, classFullName, methodName);
            // 编译源代码
            JavaStringCompiler compiler = new JavaStringCompiler();
            Map<String, byte[]> results = compiler.compile(stubSimpleName + ".java", source);
            // 加载编译好的类
            Class<?> clazz = compiler.loadClass(stubFullName, results);

            // 把Transport赋值给桩
            ServiceStub stubInstance = (ServiceStub) clazz.newInstance();
            stubInstance.setTransport(transport);
            // 返回这个桩
            return (T) stubInstance;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
