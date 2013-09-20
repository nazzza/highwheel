package org.pitest.highwheel.bytecodeparser;

import static org.pitest.highwheel.bytecodeparser.NameUtil.getElementNameForType;
import static org.pitest.highwheel.bytecodeparser.NameUtil.getOutermostClassName;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.pitest.highwheel.classpath.AccessVisitor;
import org.pitest.highwheel.model.AccessPoint;
import org.pitest.highwheel.model.AccessType;
import org.pitest.highwheel.model.ElementName;

class DependencyClassVisitor extends ClassVisitor {

  private final static ElementName OBJECT = ElementName.fromClass(Object.class);

  private final AccessVisitor      dependencyVisitor;
  private AccessPoint              parent;

  public DependencyClassVisitor(final ClassVisitor visitor,
      final AccessVisitor typeReceiver) {
    super(Opcodes.ASM4, visitor);
    this.dependencyVisitor = filterOutJavaLangObject(typeReceiver);
  }

  private static AccessVisitor filterOutJavaLangObject(final AccessVisitor child) {
    return new AccessVisitor() {

      public void apply(final AccessPoint source, final AccessPoint dest,
          final AccessType type) {
        if (!dest.getElementName().equals(OBJECT)
            && !source.getElementName().equals(dest.getElementName())) {
          child.apply(source, dest, type);
        }

      }

      public void newNode(final ElementName clazz) {
        child.newNode(clazz);

      }

      public void newEntryPoint(ElementName clazz) {
        child.newEntryPoint(clazz);        
      }

    };
  }

  @Override
  public void visit(final int version, final int access, final String name,
      final String signature, final String superName, final String[] interfaces) {
    this.parent = AccessPoint.create(getOutermostClassName(name));
    this.dependencyVisitor.newNode(this.parent.getElementName());

    if (superName != null) {
      this.dependencyVisitor.apply(this.parent,
          AccessPoint.create(getOutermostClassName(superName)),
          AccessType.INHERITANCE);
    }
    for (final String each : interfaces) {
      this.dependencyVisitor.apply(this.parent,
          AccessPoint.create(getOutermostClassName(each)), AccessType.IMPLEMENTS);
    }

    if (signature != null) {
      final SignatureReader sr = new SignatureReader(signature);
      sr.accept(new DependencySignatureVisitor(this.parent,
          this.dependencyVisitor, AccessType.SIGNATURE));
    }
  }

  @Override
  public AnnotationVisitor visitAnnotation(final String desc,
      final boolean visible) {
    this.dependencyVisitor.apply(this.parent, AccessPoint.create(ElementName
        .fromString(org.objectweb.asm.Type.getType(desc).getClassName())),
        AccessType.ANNOTATED);
    return null;
  }

  @Override
  public FieldVisitor visitField(final int access, final String name,
      final String desc, final String signature, final Object value) {
    final org.objectweb.asm.Type asmType = org.objectweb.asm.Type.getType(desc);
    this.dependencyVisitor
        .apply(this.parent, AccessPoint.create(getElementNameForType(asmType)),
            AccessType.COMPOSED);

    if (signature != null) {
      final SignatureReader sr = new SignatureReader(signature);
      sr.accept(new DependencySignatureVisitor(this.parent,
          this.dependencyVisitor, AccessType.COMPOSED));
    }

    return new DependencyFieldVisitor(this.parent, this.dependencyVisitor);
  }

  @Override
  public void visitOuterClass(final String owner, final String name,
      final String desc) {

    final ElementName outer = getOutermostClassName(owner);
    if (name != null) {
      this.parent = AccessPoint.create(outer, name);
    } else {
      this.parent = AccessPoint.create(outer);
    }
  }

  @Override
  public MethodVisitor visitMethod(final int access, final String name,
      final String desc, final String signature, final String[] exceptions) {

    final AccessPoint method = pickAccessPointForMethod(name);

    examineParameters(desc, method);
    examineExceptions(exceptions, method);
    examineReturnType(desc, method);
    
    if (isEntryPoint(access,name,desc) ) {
      this.dependencyVisitor.newEntryPoint(parent.getElementName());
    }
    
    if (signature != null) {
      final SignatureReader sr = new SignatureReader(signature);
      sr.accept(new DependencySignatureVisitor(this.parent,
          this.dependencyVisitor, AccessType.SIGNATURE));
    }

    return new DependencyMethodVisitor(method, this.dependencyVisitor);
  }

  private boolean isEntryPoint(int access, String name, String desc) {
    return isStatic(access) && name.equals("main") && desc.equals("([Ljava/lang/String;)V");
  }

  private boolean isStatic(int access) {
    return (Opcodes.ACC_STATIC & access) != 0;
  }

  private void examineReturnType(final String desc, final AccessPoint method) {
    final org.objectweb.asm.Type returnType = org.objectweb.asm.Type
        .getMethodType(desc).getReturnType();
    this.dependencyVisitor.apply(method, AccessPoint
        .create(getOutermostClassName(getElementNameForType(returnType)
            .asInternalName())), AccessType.SIGNATURE);
  }

  private void examineExceptions(final String[] exceptions,
      final AccessPoint method) {
    if (exceptions != null) {
      for (final String each : exceptions) {
        this.dependencyVisitor.apply(method,
            AccessPoint.create(getOutermostClassName(each)), AccessType.SIGNATURE);
      }
    }
  }

  private void examineParameters(final String desc, final AccessPoint method) {
    final org.objectweb.asm.Type[] params = org.objectweb.asm.Type
        .getArgumentTypes(desc);

    for (final Type each : params) {
      this.dependencyVisitor.apply(method,
          AccessPoint.create(getOutermostClassName(getElementNameForType(each)
              .asInternalName())), AccessType.SIGNATURE);
    }
  }

  private AccessPoint pickAccessPointForMethod(final String name) {
    if (parentIsMethod()) {
      return this.parent;
    }
    return this.parent.methodAccess(name);
  }

  private boolean parentIsMethod() {
    return this.parent.getAttribute() != null;
  }



}
