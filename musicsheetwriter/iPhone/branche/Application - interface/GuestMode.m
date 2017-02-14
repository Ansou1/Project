//
//  GuestMode.m
//  MSW
//
//  Created by simon on 28/01/2016.
//  Copyright (c) 2016 Score Lab. All rights reserved.
//

#import "GuestMode.h"

@implementation GuestMode

-(BOOL) CheckIfTheUserIsAGuestId:(id)object{
    if ([Id_global isEqualToString:@"0"] == true)
    {
        [self IfTheUserIsAGuest:object];
        return true;
    }
    return false;
}

-(void) IfTheUserIsAGuest:(id)object{
    // il faut ramener a l'ancienne vue ou sinon controller avant l'appuie du bouton pour evite davoir a retourner en arriere.
    // en fait il n'y a pas besoin de retourner en arriere
    // si le guest arrrive sur un truc non guest le message apparez et le return stop le code donc plus rien ne serra lisible il aura une interface vide
    //comme ça il peut cliquer partout mais rien en marchera vraiment
    //ce qui fait qu'il n'y a pas de vue bloqué par l'acces d'une autre
    UIAlertController * view2 = [UIAlertController
                                 alertControllerWithTitle:@""
                                 message:@"Certains éléments de cette partie ne sont pas accessibles en mode invité."
                                 preferredStyle:UIAlertControllerStyleAlert];
    
    UIAlertAction* butt1 = [UIAlertAction
                            actionWithTitle:@"Fermer"
                            style:UIAlertActionStyleDefault
                            handler:^(UIAlertAction * action)
                            {
                                [view2 dismissViewControllerAnimated:YES completion:nil];
                            }];
    [view2 addAction:butt1];
    [object presentViewController:view2 animated:YES completion:nil];
}

@end
