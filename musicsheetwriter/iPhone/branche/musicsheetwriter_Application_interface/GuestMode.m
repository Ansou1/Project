//
//  GuestMode.m
//  MSW
//
//  Created by simon on 28/01/2016.
//  Copyright (c) 2016 Score Lab. All rights reserved.
//

#import "GuestMode.h"

@implementation GuestMode

-(BOOL) CheckIfTheUserIsAGuest{
    if ([Id_global isEqualToString:@"0"] == true)
    {
        [self IfTheUserIsAGuest];
        return true;
    }
    return false;
}

-(void) IfTheUserIsAGuest{
    // il faut ramener a l'ancienne vue ou sinon controller avant l'appuie du bouton pour evite davoir a retourner en arriere.
    // en fait il n'y a pas besoin de retourner en arriere
    // si le guest arrrive sur un truc non guest le message apparez et le return stop le code donc plus rien ne serra lisible il aura une interface vide
    //comme ça il peut cliquer partout mais rien en marchera vraiment
    //ce qui fait qu'il n'y a pas de vue bloqué par l'acces d'une autre
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Mode Invité" message:@"Certains éléments de cette partie ne sont pas accessibles en mode invité." delegate:self cancelButtonTitle:@"Fermer" otherButtonTitles:nil];
    [alert show];
}

@end
