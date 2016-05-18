# IntentInjector

做app的时候，经常遇到这样的需求：将数据放置在Intent中，再通过startActivity方法传给下一个Activity。常用写法如下所示：

    Intent intent = new Intent(this, StickyTabActivity.class);
    intent.putExtra("string", "sdsd");
    intent.putExtra("int", 101);
    intent.putExtra("boolean", true);
    intent.putExtra("object", DummyContent.ITEMS.get(0));
    startActivity(intent);

在StickyTabActivity中，通过如下代码取出数据：

    mBoolean = getIntent().getBooleanExtra("boolean", true);

如果使用`IntentInjector`，在StickyTabActivity中，可以这样写：

    @InjectIntent("object")
    public DummyContent.DummyItem mData;

    @InjectIntent("int")
    private Integer mI;

    @InjectIntent("boolean")
    private boolean bool;

在调用:

    IntentInjector.inject(this);

后，@InjectIntent标注的成员变量即可获得传递的数据。成员变量可为private。@InjectIntent支持标注方法，如下所示：

    @InjectIntent("string")
    public void testInject(String data) {
        Log.d(TAG, "testInject: " + data);
    }

要求方法为public，仅有一个参数。

# 使用方法

## Gradle

    compile 'com.legendmohe.maven:intentinjector:0.1'
    
## maven

    <dependency>
      <groupId>com.legendmohe.maven</groupId>
      <artifactId>intentinjector</artifactId>
      <version>0.1</version>
      <type>pom</type>
    </dependency>
