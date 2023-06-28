<#if hasPackageName()>
package ${packageName};
</#if>

<#list importTypeNames as importedType>
import ${importedType};
</#list>

<#if isHaveAnnotations()>
    <#if annotationEls??>
        <#list  annotationEls as ann>
            <#if ann??>
${ann.value}
            </#if>
        </#list>
    </#if>
</#if>
<#lt>${accessibility} class ${name} <#if isHaveDecorator()>extends ${decoratorClassName} </#if> <#if isHaveImpl()>implements ${implClassName} </#if>
{
<#list fileEls as field>
    <#if field.annotationEls??>
        <#list  field.annotationEls as ann>
            <#if ann??>
    ${ann.value}
            </#if>
        </#list>
    </#if>
    ${field.accessibility}   ${field.className} ${field.name};
</#list>

<#--<#if constructor??><#nt>    <@includeModel object=constructor/></#if>-->

<#list methodEls as methodEl>
    <#if methodEl.isCreateStr()>
        ${methodEl.methodStr}
    </#if>
    <#if methodEl.isHaveDecoratorReturnStr()>
        {  return service.${methodEl.methodName}${methodEl.returStr} }
    <#elseif methodEl.isVoid()>
        { service.${methodEl.methodName}();}
    <#else >
        { return service.${methodEl.methodName}(); }
    </#if>
</#list>
}
