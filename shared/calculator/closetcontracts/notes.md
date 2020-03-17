### Notes on Results


####  ph-commons  9.0.0  > 9.3.9 Issue 1 (PRECONDITION_ADDED)

```
"file" : "com/helger/json/IJsonObject.java"
"method" : "addAll(IJsonObject)"
```

Confirmed, the parameter `@Nonnull` annotation has been replaced by `@Nonnull`.

```$java
@Nonnull default IJsonObject addAll (@Nonnull final IJsonObject aObject)
```

```$java
@Nonnull default IJsonObject addAll (@Nullable final IJsonObject aObject)
```

####  ph-commons  9.0.0  > 9.3.9 Issue 2 (POSTCONDITION_REMOVED)

```
"file" : "com/helger/commons/id/IHasID.java"
"method" : "getID()"
```

Confirmed, the JSR305 annotation has been removed. The comment in 9.3.9 still states that this *may not be null* .

```$java
@Nonnull IDTYPE getID ();
```

```$java
IDTYPE getID ();
```

####  ph-commons  9.0.0  > 9.3.9 Issue 3 (PRECONDITION_ADDED)

```
"file" : "com/helger/commons/id/factory/FileIntIDFactory.java"
"method" : "FileIntIDFactory(File, int)"
```

Confirmed, there are new precodition checks in 9.3.9, such as (line 72):

```
if (!FileHelper.canReadAndWriteFile (m_aFile))
  throw new IllegalArgumentException ("Cannot read and/or write the file " + m_aFile + "!");
if (!FileHelper.canReadAndWriteFile (m_aPrevFile))
  throw new IllegalArgumentException ("Cannot read and/or write the file " + m_aPrevFile + "!");
if (!FileHelper.canReadAndWriteFile (m_aNewFile))
  throw new IllegalArgumentException ("Cannot read and/or write the file " + m_aNewFile + "!");

if (m_aNewFile.exists ())
  throw new IllegalStateException ("The temporary ID file '" +
                                   m_aNewFile.getAbsolutePath () +
                                   "' already exists! Please use the file with the highest number. Please resolve this conflict manually.");
...
```

__NOTE: ph-commons uses its own precondition API `com.helger.commons.ValueEnforcer`! This can cause both FNs and FPs.__

####  ph-commons  9.0.0  > 9.3.9 Issue 3 (PRECONDITION_ADDED)

```
"file" : "com/helger/commons/io/stream/NonBlockingCharArrayWriter.java"
"method" : "append(CharSequence)"
```

FP (detected added FindBugs-Nullable) , the precondition JSR305Nullable is kept, but an additional precodition FindBugsNullable 
is detected due to the import `import edu.umd.cs.findbugs.annotations.SuppressFBWarnings` . 
TODO: must improve import handling as this does not import the entire package ! 

####  ph-commons  9.0.0  > 9.3.9 Issue 4 (PRECONDITION_ADDED)

```
"file" : "com/helger/commons/io/stream/NonBlockingByteArrayInputStream.java"
"method" : "NonBlockingByteArrayInputStream(byte[], int, int)"
```

Confirmed, added JSR305 @Nonnegative annotation to parameter.

```$java
public NonBlockingByteArrayInputStream (final byte [] aBuf, final int nOfs, final int nLen)
```

```$java
public NonBlockingByteArrayInputStream (@Nonnull final byte [] aBuf,
    @Nonnegative final int nOfs,
    @Nonnegative final int nLen)
```