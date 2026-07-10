# Настройка Apothic Attributes: More Customize 1.6.0

Открыть настройки:

```text
Моды → Apothic Attributes: More Customize → Настройки
```

Файл конфигурации:

```text
config/apothic_dynamic_preview-client.toml
```

Все параметры влияют только на клиентское отображение.

## Полный пример

```toml
[attribute_display_merging]
enabled = true
groups = [
  "puffish_attributes:sprinting_speed|artifacts:generic.sprinting_speed"
]
mergeOrder = "NORMAL_BEFORE_DYNAMIC"
includeChangedBaseValues = true
notifyMissingAttributes = true
suggestClosestAttribute = true
logResolvedGroups = true

[formula_display]
showFormulaText = true
showStandaloneDynamicFormulaText = true
showDynamicGroupFormulaText = true
percentDecimals = 0
constantDecimals = 2
identityEpsilon = 1.0E-9
showIdentityAsDash = true

[formula_display.scientific_notation]
scientificNotationEnabled = true
scientificStandaloneDynamicFormulas = true
scientificDynamicGroupFormulas = true
scientificPercentages = true
scientificConstants = true
scientificLargeNumbers = true
scientificIntegerDigitsThreshold = 7
scientificSmallNumbers = true
scientificLeadingFractionZerosThreshold = 4
scientificMantissaDecimals = 3
scientificTrimTrailingZeros = true
scientificNotationStyle = "E_UPPER"
scientificAlwaysShowExponentSign = true
scientificMinimumExponentDigits = 1

[highlighting]
enabled = true
highlightNonDynamicAttributes = true
highlightDynamicAttributes = true
highlightMergedGroups = true

applyExceptionsToNonDynamicAttributes = true
applyExceptionsToDynamicAttributes = true
applyExceptionsToMergedGroups = true

applyInversionToNonDynamicAttributes = true
applyInversionToDynamicAttributes = true
applyInversionToMergedGroups = true

applyBaseValuesToNonDynamicAttributes = true
applyBaseValuesToDynamicAttributes = true
applyBaseValuesToMergedGroups = true

layout = "SEPARATE"
commonHighlightMode = "STANDARD"
exceptions = []
invertedRuleAttributes = []
attributeBaseValues = [
  "minecraft:generic.movement_speed=0.1",
  "puffish_attributes:sprinting_speed=1.0"
]
evaluationBaseFallback = "REGISTERED_DEFAULT"
positiveColor = "GREEN"
negativeColor = "RED"
neutralColor = "WHITE"
```

# Типы записей

Мод различает три независимые категории:

1. одиночный нединамический атрибут;
2. одиночный динамический атрибут;
3. объединённая группа.

Переключатели группы не зависят от переключателей одиночных динамических или нединамических атрибутов.

## Нединамические атрибуты

Одиночный нединамический атрибут всегда сохраняет исходный компонент Apothic Attributes. Мод может изменить только цвет.

Группа, состоящая только из нединамических атрибутов, также сохраняет нединамический вид. Она показывает одно итоговое обычное значение, отформатированное самим Apothic Attributes. Формула `процент + константа` для такой группы не создаётся.

## Динамические записи

Формула создаётся только для:

- одиночного динамического атрибута;
- группы, содержащей хотя бы один динамический атрибут.

# Объединение групп

## `enabled`

Главный переключатель объединения. При `false` все исходные атрибуты остаются отдельными строками.

## `groups`

ID внутри группы разделяются `|`:

```toml
groups = [
  "example:first|example:second|example:third"
]
```

## `mergeOrder`

- `NORMAL_BEFORE_DYNAMIC` — сначала нединамические, затем динамические;
- `CONFIG_ORDER` — точный порядок из строки группы;
- `DYNAMIC_BEFORE_NORMAL` — сначала динамические.

## `includeChangedBaseValues`

Учитывает изменённую базу нединамического участника как константу формулы. Служебная база динамического атрибута всегда игнорируется.

# Отображение формул

## `showFormulaText`

Главный переключатель всех создаваемых формул.

## `showStandaloneDynamicFormulaText`

Отдельно включает формулу одиночного динамического атрибута.

## `showDynamicGroupFormulaText`

Отдельно включает формулу группы с динамическим участником.

Эти два параметра подчиняются `showFormulaText`. Группа только из нединамических атрибутов не использует формулу независимо от них.

## Обычное форматирование

- `percentDecimals` — число знаков после запятой у процента, когда научная запись не сработала;
- `constantDecimals` — число знаков после запятой у константы, когда научная запись не сработала;
- `identityEpsilon` — порог скрытия почти нулевых частей;
- `showIdentityAsDash` — показывать единичное преобразование как `—`.

## Научная запись

Раздел `[formula_display.scientific_notation]` применяется только к создаваемым формулам одиночных динамических атрибутов и групп с динамическим участником. Одиночные нединамические атрибуты и группы только из нединамических участников сохраняют исходный формат Apothic Attributes.

- `scientificNotationEnabled` — главный переключатель;
- `scientificStandaloneDynamicFormulas` — применять научный вид к одиночным динамическим атрибутам;
- `scientificDynamicGroupFormulas` — применять его к группам, содержащим динамические атрибуты;
- `scientificPercentages` — разрешить научный вид для процентной части;
- `scientificConstants` — разрешить его для константы;
- `scientificLargeNumbers` — обрабатывать большие числа;
- `scientificIntegerDigitsThreshold` — с какого количества цифр в целой части использовать научную запись: при значении `7` число `1000000` будет преобразовано, а `999999` ещё нет;
- `scientificSmallNumbers` — обрабатывать очень малые ненулевые числа;
- `scientificLeadingFractionZerosThreshold` — сколько нулей после запятой перед первой ненулевой цифрой требуется для преобразования: при значении `4` число `0.00001` будет показано научно;
- `scientificMantissaDecimals` — точность мантиссы, то есть число знаков после её запятой;
- `scientificTrimTrailingZeros` — убирать лишние конечные нули (`1.200E+6` → `1.2E+6`);
- `scientificNotationStyle` — `E_UPPER`, `E_LOWER` или `TIMES_TEN_CARET`;
- `scientificAlwaysShowExponentSign` — показывать `+` у положительной степени;
- `scientificMinimumExponentDigits` — минимальная ширина степени (`2` даёт `E+06`).

Примеры при стандартных настройках:

```text
123456789  → 1.235E+8
0.00001    → 1E-5
```

Очень малое значение проверяется до обычного округления, поэтому научная запись не даст ему преждевременно превратиться в ноль.

# Переключатели подсветки по категориям

## Основные

- `enabled` — общий главный переключатель;
- `highlightNonDynamicAttributes` — только одиночные нединамические атрибуты;
- `highlightDynamicAttributes` — только одиночные динамические атрибуты;
- `highlightMergedGroups` — любые объединённые группы.

Отключение подсветки группы не отключает подсветку одиночных записей и наоборот.

## Применение списка исключений

- `applyExceptionsToNonDynamicAttributes`;
- `applyExceptionsToDynamicAttributes`;
- `applyExceptionsToMergedGroups`.

Если переключатель категории выключен, содержимое `exceptions` для этой категории игнорируется.

## Применение инвертированного правила

- `applyInversionToNonDynamicAttributes`;
- `applyInversionToDynamicAttributes`;
- `applyInversionToMergedGroups`.

Если переключатель категории выключен, совпадения из `invertedRuleAttributes` для неё не инвертируют цвет.

## Применение индивидуальных баз

- `applyBaseValuesToNonDynamicAttributes`;
- `applyBaseValuesToDynamicAttributes`;
- `applyBaseValuesToMergedGroups`.

При `false` категория игнорирует `attributeBaseValues` и использует `evaluationBaseFallback`.

# Режимы цвета

## `layout = "SEPARATE"`

Процент и константа окрашиваются независимо. Например, в `120% - 3` процент может быть зелёным, а константа красной.

## `layout = "COMMON"`

Обе части получают один цвет из `commonHighlightMode`:

- `STANDARD` — подстановка базового значения;
- `PRIORITY` — сначала цвет множителя, затем константы;
- `LAYERED` — одинаковые цвета сохраняются, противоположные дают нейтральный.

Нединамическое отображение содержит одно исходное значение, поэтому для него выбирается один общий цвет.

# Списки правил

## `exceptions`

```toml
exceptions = [
  "minecraft:generic.luck",
  "apothic_attributes:*"
]
```

## `invertedRuleAttributes`

```toml
invertedRuleAttributes = [
  "minecraft:generic.gravity",
  "example:*_cost"
]
```

## `attributeBaseValues`

```toml
attributeBaseValues = [
  "minecraft:generic.movement_speed=0.1",
  "example:temperature=-20.5"
]
```

Формат: `namespace:path=value`.

## `evaluationBaseFallback`

- `REGISTERED_DEFAULT`;
- `CURRENT_BASE`;
- `ZERO`;
- `ONE`.

# Цвета

- `positiveColor`;
- `negativeColor`;
- `neutralColor`.

Доступны стандартные значения `ChatFormatting`: `GREEN`, `RED`, `WHITE`, `GOLD`, `AQUA` и остальные перечисленные в экране конфигурации.
