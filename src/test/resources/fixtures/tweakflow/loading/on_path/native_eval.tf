import core from "std";
library native {
    eval: () -> core.eval("let {f: () -> boolean via {:class 'com.twineworks.tweakflow.lang.values.NativeConstantTrue'};} f()");
}