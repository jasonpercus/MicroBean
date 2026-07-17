Add-Type -AssemblyName PresentationFramework

[xml]$xaml = @"
<Window xmlns="http://schemas.microsoft.com/winfx/2006/xaml/presentation"
        Title="Saisie"
        Height="163"
        Width="350"
        WindowStartupLocation="CenterScreen"
        ResizeMode="NoResize">
    <StackPanel Margin="20">
        <TextBlock Text="Veuillez entrer une version :" 
                   Margin="0,0,0,10"
                   HorizontalAlignment="Center"/>

        <TextBox Name="InputBox"
                 Height="25"
                 TextAlignment="Center"/>

        <Button Name="OkButton"
                Content="Valider"
                Width="80"
                Margin="0,15,0,0"
                HorizontalAlignment="Right"
                IsDefault="True"/>
    </StackPanel>
</Window>
"@

$reader = New-Object System.Xml.XmlNodeReader $xaml
$window = [Windows.Markup.XamlReader]::Load($reader)

$inputBox = $window.FindName("InputBox")
$button = $window.FindName("OkButton")

$result = ""

$button.Add_Click({
    $script:result = $inputBox.Text
    $window.Close()
})

# Met le focus dans le champ après l'affichage
$window.Add_ContentRendered({
    $inputBox.Focus()
})

$window.ShowDialog() | Out-Null

Write-Output $result