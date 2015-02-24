jaxb-ri-xjc
===========

- Generates toString, equals and hashCode methods on JAXB generated classes
- Generates Serializable
- Replaces javax.xml.datatype.XMLGregorianCalendar by java.util.Date
- Replaces java.util.List by java.util.Set on fields defined with maxOccurs="unbounded"

Usage overview
===========

toString : 
- By default, uses all the fields to generate toString,
- Use @ExcludeOnToString annotation to exclude fields,
- Activate the plugin using -XvsToString-switch.

equals and hashCode :
- Use @IncludeOnEqualsAndHash annotation to include fields,
- Activate the plugin using -XvsEqualsHashCode-switch.

Serializable
- Generates ... "implements Serializable" in all classes,
- Activate the plugin using -XvsSerializable-switch.

Date replacement
- Replaces javax.xml.datatype.XMLGregorianCalendar by java.util.Date,
- Activate the plugin using -XvsDate,

Unbounded as Set
- Replaces java.util.List by java.util.Set on fields defined with maxOccurs="unbounded"
- Activate the plugin using -XvsHashset,

Current limitations
===========
Uses Annox (https://github.com/highsource/jaxb2-annotate-plugin#usage-overview) to define the @ExcludeOnToString and @IncludeOnEqualsAndHash in binding files or directly in schema.


Configuration
===========

XSD file
===========
```xsd
<?xml version="1.0" encoding="UTF-8"?>
<xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema"
	elementFormDefault="qualified" attributeFormDefault="qualified"
	xmlns:jaxb="http://java.sun.com/xml/ns/jaxb"	
	targetNamespace="http://www.xjc.vss.pt"
	xmlns:tns="http://www.xjc.vss.pt">

	<xsd:complexType name="AbstractEntity" abstract="true">
		<xsd:attribute name="id" type="xsd:string"/>	
	</xsd:complexType>	
	
	<xsd:element name="User">
		<xsd:complexType>
			<xsd:complexContent>
				<xsd:extension base="tns:AbstractEntity">
					<xsd:sequence>
						<xsd:element name="firstName" type="xsd:string"/>
						<xsd:element name="lastName" type="xsd:string"/>
						<xsd:element name="username" type="xsd:string"/>
						<xsd:element name="password" type="xsd:string"/>
						<xsd:element name="created" type="xsd:date"/>
						<xsd:element name="updated" type="xsd:date"/>						
						<xsd:element name="roles" type="xsd:string" maxOccurs="unbounded"/>
						<xsd:element name="permissions" type="xsd:string" maxOccurs="unbounded"/>						
					</xsd:sequence>
				</xsd:extension>
			</xsd:complexContent>
		</xsd:complexType>
	</xsd:element>	
</xsd:schema>
```

Bindings file (bindings.xjb)
===========
```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<jaxb:bindings
    xmlns:jaxb="http://java.sun.com/xml/ns/jaxb" xmlns:xsd="http://www.w3.org/2001/XMLSchema"
    xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:annox="http://annox.dev.java.net"
    xsi:schemaLocation="http://java.sun.com/xml/ns/jaxb http://java.sun.com/xml/ns/jaxb/bindingschema_2_0.xsd"
    jaxb:extensionBindingPrefixes="xjc annox"
    version="2.1">

    <jaxb:bindings schemaLocation="User.xsd" node="/xsd:schema">
	
		<!-- Annotate Abstract Class -->
        <jaxb:bindings node="xsd:complexType[@name='AbstractEntity']/xsd:attribute[@name='id']">
			<annox:annotate target="field">@pt.vss.xjc.annotation.IncludeOnEqualsAndHash</annox:annotate>
        </jaxb:bindings>
		
		<!-- Annotate Class User -->		
        <jaxb:bindings node="xsd:element[@name='User']/xsd:complexType/xsd:complexContent/xsd:extension/xsd:sequence/xsd:element[@name='username']">
            <annox:annotate target="field">@pt.vss.xjc.annotation.IncludeOnEqualsAndHash</annox:annotate>
        </jaxb:bindings>
		
        <jaxb:bindings node="xsd:element[@name='User']/xsd:complexType/xsd:complexContent/xsd:extension/xsd:sequence/xsd:element[@name='password']">
            <annox:annotate target="field">@pt.vss.xjc.annotation.ExcludeOnToString</annox:annotate>
        </jaxb:bindings>
		
    </jaxb:bindings>

</jaxb:bindings>
```

Maven configuration Example
===========
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
   <modelVersion>4.0.0</modelVersion>
   <groupId>com.github.vsspt</groupId>
   <artifactId>xjc-code-generator-tests</artifactId>
   <version>1.1.0</version>
   <packaging>jar</packaging>
   <name>Jaxb-Ri Plugin - toString, equals and hashCode Generation Test Project</name>
   <url>https://github.com/vsspt/jaxb-ri-xjc/tree/master/sample-projects/code-generator</url>
   <properties>
      <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
   </properties>   
   <dependencies>
      <dependency>
         <groupId>com.github.vsspt</groupId>
         <artifactId>jaxb-ri-xjc</artifactId>
         <version>1.1.1</version>
      </dependency>
   </dependencies>
   <build>
      <finalName>${project.artifactId}-${project.version}</finalName>
      <plugins>
         <plugin>
            <groupId>org.jvnet.jaxb2.maven2</groupId>
            <artifactId>maven-jaxb2-plugin</artifactId>
            <configuration>
               <extension>true</extension>
               <args>
                  <arg>-Xannotate</arg>
                  <arg>-XvsEqualsHashCode</arg>
                  <arg>-XvsToString</arg>
                  <arg>-XvsSerializable</arg>
				  <arg>-XvsDate</arg>				  
				  <arg>-XvsHashset</arg>					  
               </args>
               <plugins>
                  <plugin>
                     <groupId>org.jvnet.jaxb2_commons</groupId>
                     <artifactId>jaxb2-basics-annotate</artifactId>
                     <version>1.0.1</version>
                  </plugin>
                  <plugin>
                     <groupId>org.jvnet.jaxb2_commons</groupId>
                     <artifactId>jaxb2-annotate-plugin-test-annox-annotations</artifactId>
                     <version>1.0.0</version>
                  </plugin>
                  <plugin>
                     <groupId>com.github.vsspt</groupId>
                     <artifactId>jaxb-ri-xjc</artifactId>
                     <version>1.1.1</version>
                  </plugin>
               </plugins>
            </configuration>
            <executions>
               <execution>
                  <goals>
                     <goal>generate</goal>
                  </goals>
               </execution>
            </executions>
         </plugin>
         <plugin>
            <inherited>true</inherited>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
         </plugin>
      </plugins>
   </build>
</project>
```

Maven Dependency
===========
```xml
<dependency>
 <groupId>com.github.vsspt</groupId>
 <artifactId>jaxb-ri-xjc</artifactId>
 <version>1.1.1</version>
</dependency>
```	

What's new
===========

Version 2.0
===========
Removed Setter option
Added XMLGregorianCalendar replacement functionality
Added List replacement functionality

Version 1.1.1
===========
Small corrections

Version 1.1.0
===========
Added Setter functionality

Version 1.0.0
===========
Initial deployment
