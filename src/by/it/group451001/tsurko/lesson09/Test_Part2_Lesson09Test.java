//package by.it.a_khmelev.lesson09;//


import by.it.HomeWork;
@@ -206,30 +206,30 @@

private boolean notComparable(Method m) {
    return m.getReturnType() != Comparable.class &&
            0 == Arrays.stream(m.getParameterTypes())
                    .filter(p -> p == Comparable.class)
                    .count();
}

private String getSignature(Method method) {
    return cache.computeIfAbsent(method, m -> {
        Class<?>[] parameterTypes = method.getParameterTypes();
        StringJoiner out = new StringJoiner(
                ",",
                method.getReturnType().getSimpleName() + " " + method.getName() + "(",
                ")"
        );
        for (int i = 0, parameterTypesLength = parameterTypes.length; i < parameterTypesLength; i++) {
            out.add(parameterTypes[i].getSimpleName());
        }
        return out.toString();
    });
}


public String getSignatures(Class<?> aClass) {
    return Stream.of(aClass.getMethods(), aClass.getDeclaredMethods())
            .flatMap(Arrays::stream)
            .distinct()
            .filter(m -> !Modifier.isStatic(m.getModifiers()))
            .map(this::getSignature)